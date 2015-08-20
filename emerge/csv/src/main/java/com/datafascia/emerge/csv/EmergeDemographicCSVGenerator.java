// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.common.jackson.CSVMapper;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.domain.model.HumanName;
import com.datafascia.emerge.models.Demographic;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * The main application class for the Emerge CSV generator.
 */
@Slf4j
public class EmergeDemographicCSVGenerator {

  private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
  // Date format the CSV is expecting.
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;
  // Name format
  private static final String NAME_FMT =
      "<[" + HumanName.GIVEN + "," + HumanName.FAMILY + "]; separator=\" \">";

  private static final String HEIGHT = "304894102";
  private static final String WEIGHT = "WT";
  private static final BigDecimal CENTIMETER_PER_INCH = new BigDecimal("2.54");

  private static CSVMapper<Demographic> mapper;
  private static FhirClient client;

  // Private constructor disallows creating instances of this class
  private EmergeDemographicCSVGenerator() {
  }

  /**
   * Generates Emerge CSV file.
   *
   * @param apiEndpoint
   *     API endpoint URI
   * @param user
   *     user name for API access
   * @param password
   *     password for API access
   * @param csvFile
   *     output CSV file name
   *
   * @throws java.io.IOException errors writing to CSV file
   */
  public static void generate(URI apiEndpoint, String user, String password, String csvFile)
      throws IOException {

    log.info("Command parameters:");
    log.info("API Endpoint: {}", apiEndpoint);
    log.info("CSV File: {}", csvFile);
    log.info("Username: {}", user);
    log.info("Password: {}", password);

    mapper = new CSVMapper<>(Demographic.class);
    client = new FhirClient(apiEndpoint.toString(), user, password);

    try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
      pw.println(Joiner.on(",").join(mapper.getHeaders()));

      // Get all in-progress encounters
      List<Encounter> encounters = client.getInProgressEncounters();
      int entry = 1;
      for (Encounter encounter: encounters) {
        UnitedStatesPatient patient = client.getPatientFromEncounter(encounter);
        if (null != patient) {
          pw.println(mapper.asCSV(getDemographic(patient, encounter, entry)));
          entry++;
        }
      }
    }
  }

  /**
   * Take a patient object and entry number, and return a populated Demographic object.
   *
   * @param patient
   *     patient object
   * @param encounter
   *     encounter
   * @param entry
   *     the entry number in the CSV file
   * @return the Demographic object.
   */
  private static Demographic getDemographic(
      UnitedStatesPatient patient, Encounter encounter, int entry) {
    Demographic demo = new Demographic();
    demo.setEntry(Integer.toString(entry));

    LocalDateTime now = LocalDateTime.now(TIME_ZONE);
    demo.setDateCreated(DATE_TIME_FORMATTER.format(now));
    demo.setDataCollectionDate(DATE_FORMATTER.format(now));

    addPatientData(demo, patient, encounter);
    addEncounterData(demo, encounter);

    return demo;
  }

  /**
   * Populate the demographic data with the patient object.
   *
   * @param demo
   *     populate demographic record
   * @param patient
   *     read data from patient
   */
  private static void addPatientData(
      Demographic demo, UnitedStatesPatient patient, Encounter encounter) {
    demo.setGender(client.getPatientGender(patient).equals("female") ? "Female" : "Male");
    demo.setPatientDateOfBirth(client.getPatientDateOfBirth(patient));
    demo.setSubjectPatientId(client.getInstitutionPatientId(patient));
    demo.setSubjectPatcom(client.getEncounterIdentifier(encounter));

    Optional<String> patientName = client.getPatientName(patient);
    if (patientName.isPresent()) {
      demo.setPatientName(patientName.get());
    }

    // Translate the race enumeration
    switch (client.getPatientRace(patient)) {
      case AMERICAN_INDIAN:
        demo.setRace("I");
        break;
      case ASIAN:
        demo.setRace("N");
        break;
      case BLACK:
        demo.setRace("B");
        break;
      case OTHER:
        demo.setRace("O");
        break;
      case PACIFIC_ISLANDER:
        demo.setRace("P");
        break;
      case WHITE:
        demo.setRace("W");
        break;
      default:
        demo.setRace("U");
    }
  }

  /**
   * Populate the demographic data with the encounter object.
   *
   * @param demo
   *     populate demographic record
   * @param encounter
   *     read data from encounter
   */
  private static void addEncounterData(Demographic demo, Encounter encounter) {
    demo.setSicuAdmissionDate(client.getPatientAdmissionDate(encounter));

    List<Observation> observations = client.getObservations(encounter);
    log.info("number of observations: {}", observations.size());
    for (Observation observation: observations) {
      Optional<String> observationCode = client.getObservationCode(observation);
      if (observationCode.isPresent()) {
        switch (observationCode.get()) {
          case HEIGHT:
            Optional<String> height = client.getObservationQuantityValue(observation);
            if (height.isPresent()) {
              String units = client.getObservationUnits(observation);
              if (!Strings.isNullOrEmpty(units)) {
                if (units.equals("in")) {
                  // UCSF stores height in inches, but the CSV expects centimeters.
                  BigDecimal heightInch = new BigDecimal(height.get());
                  Integer heightCm = heightInch.multiply(CENTIMETER_PER_INCH).intValue();
                  demo.setPatientAdmissionHeightCm(heightCm.toString());
                } else if (units.equals("cm")) {
                  demo.setPatientAdmissionHeightCm(height.get());
                } else {
                  log.debug("unknown units for height: {}", units);
                  demo.setPatientAdmissionHeightCm("0");
                }
              } else {
                log.debug("unspecified units for height");
                demo.setPatientAdmissionHeightCm("0");
              }
            }
            break;

          case WEIGHT:
            Optional<String> weight = client.getObservationQuantityValue(observation);
            if (weight.isPresent()) {
              demo.setPatientAdmissionWeightKg(weight.get());
            }
            break;

          default:
            // Do nothing
        }
      }
    }
  }
}
