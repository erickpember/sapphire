// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.scenarios;

import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.common.io.ResourceUtils;
import com.datafascia.models.Patient;
import com.datafascia.shell.Main;
import com.google.common.io.Resources;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Scenario tests
 */
@Slf4j
public class ScenariosIT {
  private static final DateTimeFormatter dateFormat
      = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  private static String mllpIngestCmd = "ingest-hl7-files";

  private String mllpHost = null;
  private String mllpPort = null;
  private static DatafasciaApi api;

  /**
   * Prepare the integration test to run.
   *
   * @throws Exception
   */
  @BeforeTest
  public void before() throws Exception {
    mllpHost = System.getProperty("mllpHost");
    mllpPort = System.getProperty("mllpPort");
    String apiURI = System.getProperty("apiURI");
    String user = System.getProperty("user");
    String password = System.getProperty("password");
    log.info("MLLP host: {}, port: {}", mllpHost, mllpPort);
    log.info("API URI: {}", apiURI);
    log.info("User name: {}, password: {}", user, password);

    // Create the REST API end point
    api = DatafasciaApiBuilder.endpoint(new URI(apiURI), user, password);
  }

  /**
   * Read the list of scenarios from resources.
   *
   * @returns An iterator for an Object[]
   */
  @DataProvider(name = "scenariosList")
  public Iterator<Object[]> getScenarios() throws Exception {
    List<Object[]> result = new ArrayList<Object[]>();

    String line = null;
    try (BufferedReader reader = new BufferedReader(
        new StringReader(ResourceUtils.resource("scenariosList.txt")))) {
      while ((line = reader.readLine()) != null) {
        result.add(new Object[] {line});
      }
    } catch (IOException e) {
      log.error("IO Exception: {}", e);
    }

    return result.iterator();
  }

  /**
   * Fetch admitted patients and validate them.
   *
   * @param The scenario to run
   *
   * @throws Exception
   */
  @Test(dataProvider = "scenariosList")
  public void testScenarios(String scenario) throws Exception {
    log.info("executing testScenario: {}", scenario);
    processScenario(scenario);
  }

  /**
   * Process the scenario file.
   *
   * @param The scenario file
   *
   * @throws Exception
   */
  private void processScenario(String scenarioFile) throws Exception {
    String line = null;
    try (BufferedReader reader = new BufferedReader(
        new StringReader(ResourceUtils.resource("scenarios/" + scenarioFile)))) {
      while ((line = reader.readLine()) != null) {
        log.info("executing step: {}", line);
        String[] stepComps = line.split("\\|");
        ingestHL7Message(stepComps[0], Integer.parseInt(stepComps[2]));
        validateStep(stepComps[1]);
      }
    } catch (IOException e) {
      log.error("IO Exception: {}", e);
    }
  }

  /**
   * Execute the command to ingest the HL7 message
   *
   * @param The HL7 message file name
   * @param The number of seconds to wait after sending the file to allow system to process
   *
   * @throws Exception
   */
  private void ingestHL7Message(String filename, int delay) throws Exception {
    String resourceFile = Resources.getResource("hl7Messages/" + filename).getPath();
    String[] shellArgs = {mllpIngestCmd, "--files", resourceFile, "-h", mllpHost, "-p", mllpPort};
    Main.main(shellArgs);
    TimeUnit.SECONDS.sleep(delay);
  }

  /**
   * Fetch patients and validate them.
   *
   * @param The patient ID
   */
  private void validateStep(String mrn) {
    for (Patient pat : api.patients(null, true, 1).getCollection()) {
      if (pat.getId().toString().equals(mrn)) {
        validatePatient(pat, "NATUS-ADULT", "ONE", "A",
            LocalDateTime.parse("1984-10-01T05:00:00Z", dateFormat).toLocalDate(), mrn, mrn);
        return;
      }
    }
    fail("Patient " + mrn + " not found");
  }

  /**
   * Validates a patient object against various expected values.
   *
   * @param patient object
   * @param lastName last name
   * @param firstName first name
   * @param middleName middle name
   * @param birthDate date of birth
   * @param patId patient dF identifier
   * @param instId patient institution identifier
   */
  private void validatePatient(Patient patient, String lastName, String firstName,
      String middleName, LocalDate birthDate, String patId, String instId) {
    assertEquals(patient.getName().getFirst(), firstName);
    assertEquals(patient.getName().getMiddle(), middleName);
    assertEquals(patient.getName().getLast(), lastName);
    assertEquals(patient.getBirthDate(), birthDate);
    assertEquals(patient.getId().toString(), patId);
    assertEquals(patient.getInstitutionPatientId(), instId);
  }
}
