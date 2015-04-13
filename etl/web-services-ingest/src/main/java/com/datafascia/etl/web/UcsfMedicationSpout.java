// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.web;

import com.codahale.metrics.MetricRegistry;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.FixedAuthorizationsSupplier;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.persist.Id;
import com.datafascia.common.web.RestUtils;
import com.datafascia.domain.model.Patient;
import com.datafascia.domain.persist.PatientRepository;
import java.io.IOException;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;

/**
 * A spout for UCSF medication data.
 *
 * It queries the UCSF medication service patient-by-patient for latest medication data and emits to
 * the topology. The service it connects to is a REST service from which a JSON response is
 * expected. The patient list and their associated visits are pulled from the PatientRepository.
 */
@Slf4j
public class UcsfMedicationSpout extends AbstractWebSpout {
  public static final String medJsonField = "medJson";
  private String restBase;
  private String instanceName;
  private String zookeeper;
  private String username;
  private String password;
  private Duration duration;
  private Map<Id<Patient>, Instant> lastPolls = new HashMap<>();

  /**
   * Creates a UcsfMedicationSpout
   * @param instanceName The instance to connect to.
   * @param zookeeper The zookeepers to connect to.
   * @param username Username to connect with.
   * @param password Password for the user.
   * @param restBase Base address of the web service.
   * @param duration Duration between polls.
   */
  public UcsfMedicationSpout(String instanceName, String zookeeper, String username,
      String password, String restBase, Duration duration) {
    this.instanceName = instanceName;
    this.zookeeper = zookeeper;
    this.username = username;
    this.password = password;
    this.duration = duration;
    this.restBase = restBase;
  }

  /**
   * Returns a usable PatientRepository
   * @return A usable PatientRepository
   * @throws AccumuloSecurityException When provided credentials are incorrect.
   * @throws AccumuloException When unable to connect to accumulo.
   */
  public PatientRepository getRepo() throws AccumuloSecurityException, AccumuloException {
    Instance instance = new ZooKeeperInstance(instanceName, zookeeper);
    Connector connector = instance.getConnector(username, new PasswordToken(password));
    AccumuloTemplate template = new AccumuloTemplate(connector, new FixedColumnVisibilityPolicy()
        , new FixedAuthorizationsSupplier(), new MetricRegistry());
    return new PatientRepository(template);
  }

  @Override
  protected List<String> getFields() {
    return Arrays.asList(new String[] {medJsonField});
  }

  @Override
  protected Instant getNextPoll(boolean isFirstRun) {
    if (isFirstRun) {
      return Instant.now();
    }

    return Instant.now().plus(duration);
  }

  @Override
  protected List<String> getResponses() {
    try {
      return getMedResponses(getActivePatients());
    } catch (IOException e) {
      log.error("Unable to connect to given service.", e);
    } catch (AccumuloSecurityException e) {
      log.error("Bad credentials connecting to accumulo.", e);
    } catch (AccumuloException e) {
      log.error("Unable to connect to accumulo.", e);
    } catch (UnrecoverableKeyException | CertificateException | NoSuchAlgorithmException
        | KeyStoreException | KeyManagementException e) {
      log.error("Error establishing TLS connection to medications service.", e);
    }

    return null;
  }

  private List<Patient> getActivePatients() throws AccumuloSecurityException, AccumuloException {
    List<Patient> patients = new ArrayList<>();
    Id<Patient> lastPatient = null;

    while (true) {
      Optional<Id<Patient>> optLastPatient = lastPatient == null ? Optional.empty() : Optional.of
          (lastPatient);
      Collection<Patient> patientCollection = getRepo().list(optLastPatient,
          Optional.of(Boolean.TRUE), 1000);
      if (patientCollection.isEmpty() || (patientCollection.size() == 1 && lastPatient != null)) {
        break;
      }

      for (Patient patient : patientCollection) {
        if (!patients.contains(patient)) {
          patients.add(patient);
        }
      }

      lastPatient = patients.get(patients.size() - 1).getId();
    }

    return patients;
  }

  private List<String> getMedResponses(List<Patient> patients) throws IOException,
      UnrecoverableKeyException, CertificateException, NoSuchAlgorithmException, KeyStoreException,
      KeyManagementException {
    List<String> responses = new ArrayList<>();

    // Get last encounters and fetch medication data.
    for (Patient patient : patients) {
      Instant lastPoll = lastPolls.get(patient.getId());
      lastPolls.put(patient.getId(), Instant.now());
      if (lastPoll == null) {
        lastPoll = Instant.MIN;
      }

      if (patient.getLastEncounterId() != null) {
        log.info("Polling medication data for " + patient.getLastEncounterId().toString());

        String response = RestUtils.getJson(restBase + "?CSN="
            + URLEncoder.encode(patient.getLastEncounterId().toString(), "UTF-8"), SSLContext
            .getDefault().getSocketFactory());

        response = "{\"patient:\":\"" + patient.getId().toString() + "\",\"encounter\":\"" +
            patient.getLastEncounterId().toString() + "\",\"medications\":" + response + "}";
        responses.add(response);
      }
    }

    return responses;
  }
}
