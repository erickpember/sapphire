// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.scenarios;

import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.common.io.ResourceUtils;
import com.datafascia.common.jackson.DFObjectMapper;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Patient;
import com.datafascia.shell.Main;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.io.Resources;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Scenario tests
 */
@Slf4j
public class ScenariosIT {
  private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");
  private static final DateTimeFormatter DATE_FORMATTER
      = DateTimeFormatter.ISO_LOCAL_DATE;
  private static final DateTimeFormatter dateFormat
      = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");
  private static final String mllpIngestCmd = "ingest-hl7-files";

  private String mllpHost = null;
  private String mllpPort = null;
  private static DatafasciaApi api;
  private static ObjectMapper mapper = DFObjectMapper.objectMapper();

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
   * Execute scenario #1.
   *
   * @throws Exception
   */
  @Test
  public void testScenario1() throws Exception {
    log.info("Executing unknown-female-adm-upd-obs-dis.json");
    processScenario("unknown-female-adm-upd-obs-dis.json",
        "NATUS-ADULT", "ONE", "A", "FEMALE", "UNKNOWN",
        LocalDateTime.parse("1984-10-01T05:00:00Z", dateFormat).toLocalDate(),
        LocalDateTime.parse("2014-10-01T19:01:00Z", dateFormat).toLocalDate());
  }

  /**
   * Execute scenario #2.
   *
   * @throws Exception
   */
  @Test
  public void testScenario2() throws Exception {
    log.info("Executing white-male-adm-xfr-upd-dis.json");
    processScenario("white-male-adm-xfr-upd-dis.json",
        "TEST", "INPBM", "", "MALE", "WHITE",
        LocalDateTime.parse("1970-05-01T05:00:00Z", dateFormat).toLocalDate(),
        LocalDateTime.parse("2014-12-23T18:08:00Z", dateFormat).toLocalDate());
  }

  /**
   * Execute scenario #3.
   *
   * @throws Exception
   */
  @Test
  public void testScenario3() throws Exception {
    log.info("Executing white-female-adm-xfr-upd-dis.json");
    processScenario("white-female-adm-xfr-upd-dis.json",
        "TEST", "OUTPTBM", "", "FEMALE", "WHITE",
        LocalDateTime.parse("1980-05-01T05:00:00Z", dateFormat).toLocalDate(),
        LocalDateTime.parse("2014-12-23T18:13:00Z", dateFormat).toLocalDate());
  }

  /**
   * Execute scenario #4.
   *
   * @throws Exception
   */
  @Test
  public void testScenario4() throws Exception {
    log.info("Executing white-male-child-adm-xfr-upd-obs-dis.json");
    processScenario("white-male-child-adm-xfr-upd-obs-dis.json",
        "TEST", "CHILDBM", "", "MALE", "WHITE",
        LocalDateTime.parse("2000-05-01T05:00:00Z", dateFormat).toLocalDate(),
        LocalDateTime.parse("2014-12-23T19:55:00Z", dateFormat).toLocalDate());
  }

  /**
   * Execute scenario #5.
   *
   * @throws Exception
   */
  @Test
  public void testScenario5() throws Exception {
    log.info("Executing nativeAmerican-male-adm-upd-dis.json");
    processScenario("nativeAmerican-male-adm-upd-dis.json",
        "MB-HIM", "FOUR", "A", "MALE", "AMERICAN_INDIAN",
        LocalDateTime.parse("2009-02-19T05:00:00Z", dateFormat).toLocalDate(),
        LocalDateTime.parse("2015-02-19T21:24:00Z", dateFormat).toLocalDate());
  }

  /**
   * Execute scenario #40.
   *
   * @throws Exception
   */
  @Test
  public void testScenario40() throws Exception {
    log.info("Executing asian-female-infant-adm-xfr-upd-xfr-upd.json");
    processScenario("asian-female-infant-adm-xfr-upd-xfr-upd.json",
        "TRN-TWO", "GIRL", "", "FEMALE", "ASIAN",
        LocalDateTime.parse("2015-01-02T05:00:00Z", dateFormat).toLocalDate(),
        LocalDateTime.parse("2015-01-02T21:42:00Z", dateFormat).toLocalDate());
  }

  /**
   * Process the scenario file.
   *
   * A scenario file is a JSON file which defines the complete scenario to test. A scenario
   * comprises the description along with all the steps that need to be executed. Each step
   * contains the following information:
   *
   * Step Description     - a short description of the scenario step
   * HL7 Message File     - the HL7 base file name from the resources directory
   * Patient ID           - the internal patient identifier
   * Intition Pattient Id - the institution patient identifier
   * Ingest Delay         - the number of seconds to pause after ingesting the HL7 file
   * Active Status        - whether or not the patient is active
   *
   * @param scenarioFile scenario file
   * @param lastName patient last name
   * @param firstName patient first name
   * @param middleName patient middle name
   * @param gender patient gender
   * @param race patient race
   * @param birthDate patient date of birth
   * @param admitDate patient admittance date
   *
   * @throws Exception
   */
  private void processScenario(String scenarioFile, String lastName, String firstName,
      String middleName, String gender, String race, LocalDate birthDate, LocalDate admitDate)
      throws Exception {
    String text = ResourceUtils.resource("scenarios/" + scenarioFile);
    Scenario scenario = mapper.readValue(text, Scenario.class);
    log.info(scenario.getDescription());
    for (ScenarioStep step : scenario.getSteps()) {
      log.info("Executing step: {}", step.getDescription());
      ingestHL7Message(step.getMessageFile(), step.getWaitInterval());
      validateStep(lastName, firstName, middleName, gender, race, birthDate, admitDate,
          scenario.getPatientId(), scenario.getInstitutionId(), step.getPatientStatus());
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
   * Fetch the requested patient and validate patient data, active status and admit time.
   *
   * @param The patient ID
   * @param lastName patient last name
   * @param firstName patient first name
   * @param middleName patient middle name
   * @param gender patient gender
   * @param race patient race
   * @param birthDate patient date of birth
   * @param admitDate patient admittance date
   * @param patientId internal unique patient identifier
   * @param institutionPatientId the institution patient identifier
   * @param status patient status flag
   */
  private void validateStep(String lastName, String firstName, String middleName,
      String gender, String race, LocalDate birthDate, LocalDate admitDate,
      String patientId, String institutionPatientId, boolean active) {
    log.info("Looking for {} and active {}", patientId, active);
    for (Patient pat : api.patients(patientId, active, 1).getCollection()) {
      if (pat.getId().toString().equals(patientId)) {
        validatePatient(pat, lastName, firstName, middleName, gender, race, birthDate,
            patientId, institutionPatientId);
        validateEncounter(pat, admitDate);
        return;
      }
    }
    fail("Patient " + patientId + " not found");
  }

  /**
   * Validates a patient object against various expected values.
   *
   * @param patient object
   * @param lastName last name
   * @param firstName first name
   * @param middleName middle name
   * @param gender patient gender
   * @param race patient race
   * @param birthDate date of birth
   * @param patientId patient dF identifier
   * @param institutionPatientId patient institution identifier
   */
  private void validatePatient(Patient patient, String lastName, String firstName,
      String middleName, String gender, String race,
      LocalDate birthDate, String patientId, String institutionPatientId) {
    assertEquals(patient.getName().getFirst(), firstName);
    assertEquals(patient.getName().getMiddle(), middleName);
    assertEquals(patient.getName().getLast(), lastName);
    assertEquals(patient.getBirthDate(), birthDate);
    assertEquals(patient.getRace().toString(), race);
    assertEquals(patient.getGender().toString(), gender);
    assertEquals(patient.getId().toString(), patientId);
    assertEquals(patient.getInstitutionPatientId(), institutionPatientId);
  }

  /**
   * Validates a patient encouter against various expected values.
   *
   * @param patient object
   */
  private void validateEncounter(Patient patient, LocalDate expectedTime) {
    // Get the last encounter for the patient
    Encounter encounter = api.lastvisit(patient.getId().toString());
    if (encounter != null && encounter.getPeriod().getStartInclusive() != null) {
      ZonedDateTime admitTime = ZonedDateTime.ofInstant(encounter.getPeriod().getStartInclusive(),
          TIME_ZONE);
      assertEquals(DATE_FORMATTER.format(admitTime), expectedTime.toString());
    } else {
      if (encounter == null) {
        log.info("null encounter");
      } else if (encounter.getPeriod().getStartInclusive() == null) {
        log.info("null start");
      }
      fail("Invalid Admission Date for " + patient.getId().toString());
    }
  }
}
