// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.scenarios.patient;

import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.domain.model.PagedCollection;
import com.datafascia.domain.model.Patient;
import com.datafascia.shell.Main;
import com.google.common.io.Resources;
import java.net.URI;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Acceptance test for patient processing.
 */
@Slf4j
public class PatientIT {

  private static final String INGEST_HL7_COMMAND = "ingest-hl7-files";
  private static final String PATIENT_ID = "urn:df-patientId-1:97552037";

  private static String mllpHost;
  private static String mllpPort;
  private static DatafasciaApi apiClient;

  @BeforeClass
  public static void beforeClass() throws Exception {
    mllpHost = System.getProperty("mllpHost");
    mllpPort = System.getProperty("mllpPort");
    log.info("MLLP host: {}, port: {}", mllpHost, mllpPort);

    String apiURI = System.getProperty("apiURI");
    String user = System.getProperty("user");
    String password = System.getProperty("password");
    log.info("API URI: {}", apiURI);
    log.info("User name: {}, password: {}", user, password);

    apiClient = DatafasciaApiBuilder.endpoint(new URI(apiURI), user, password);
  }

  private void ingestHL7Message(String filename, int delaySeconds) throws Exception {
    String resourceFile = Resources.getResource(PatientIT.class, filename).getPath();
    String[] shellArgs = {
        INGEST_HL7_COMMAND, "--files", resourceFile, "-h", mllpHost, "-p", mllpPort };
    Main.main(shellArgs);
    TimeUnit.SECONDS.sleep(delaySeconds);
  }

  @Test
  public void should_deactivate_patient() throws Exception {
    // Given a patient was admitted
    apiClient.deletePatient(PATIENT_ID);
    ingestHL7Message("ADT_A01.hl7", 3);

    // When a discharge patient message is ingested
    ingestHL7Message("ADT_A03.hl7", 3);

    // Then the patient is deactivated
    PagedCollection<Patient> patients = apiClient.patients(PATIENT_ID, false, 1);
    Patient patient = patients.getCollection().iterator().next();

    assertEquals(patient.isActive(), false);
  }
}
