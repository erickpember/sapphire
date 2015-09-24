// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.common.jackson.CSVMapper;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.models.DailyProcess;
import com.google.common.base.Joiner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
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
public class EmergeDailyProcessCSVGenerator {

  private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
  // Date format the CSV is expecting.
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  private static CSVMapper<DailyProcess> mapper;
  private static FhirClient client;

  private static LocalDateTime now;

  // Private constructor disallows creating instances of this class
  private EmergeDailyProcessCSVGenerator() {
  }

  /**
   * Generates Emerge CSV file.
   *
   * @param institution
   *     the source institution
   * @param apiEndpoint
   *     API endpoint URI
   * @param user
   *     user name for API access
   * @param password
   *     password for API access
   * @param csvFile
   *     output CSV file name
   * @throws IOException if error writing to CSV file
   */
  public static void generate(String institution, URI apiEndpoint, String user, String password,
      String csvFile) throws IOException {

    log.info("Command parameters:");
    log.info("Institution: {}", institution);
    log.info("API Endpoint: {}", apiEndpoint);
    log.info("CSV File: {}", csvFile);
    log.info("Username: {}", user);
    log.info("Password: {}", password);

    mapper = new CSVMapper<>(DailyProcess.class);
    client = new FhirClient(apiEndpoint.toString(), user, password);

    try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
      pw.println(Joiner.on(",").join(mapper.getHeaders()));

      List<Encounter> encounters = client.getInProgressEncounters(institution);
      int entry = 1;
      for (Encounter encounter: encounters) {
        UnitedStatesPatient patient = client.getPatientFromEncounter(encounter);
        if (null != patient) {
          pw.println(mapper.asCSV(getDailyProcess(patient, encounter, entry)));
          entry++;
        }
      }
    }
  }

  /**
   * Take a patient object and entry number, and return a populated DailyProcess object.
   *
   * @param patient
   *     patient
   * @param encounter
   *     encounter
   * @param entry
   *     entry number (Patcom in the CSV)
   * @return the DailyProcess object.
   */
  private static DailyProcess getDailyProcess(
      UnitedStatesPatient patient, Encounter encounter, int entry) {
    DailyProcess dailyProcess = new DailyProcess();
    dailyProcess.setEntry(Integer.toString(entry));

    now = LocalDateTime.now(TIME_ZONE);
    dailyProcess.setDateCreated(DATE_TIME_FORMATTER.format(now));
    dailyProcess.setDataCollectionDate(DATE_FORMATTER.format(now));

    addPatientData(dailyProcess, patient, encounter);
    addObservationData(dailyProcess, encounter);
    addMedicationPrescriptionData(dailyProcess, encounter);
    return dailyProcess;
  }

  /**
   * Populate the daily process data with patient data.
   *
   * @param dailyProcess
   *     daily process data
   * @param patient
   *     the patient resource
   * @param encounter
   *     the current patient encounter resource
   */
  private static void addPatientData(
      DailyProcess dailyProcess, UnitedStatesPatient patient, Encounter encounter) {
    dailyProcess.setSubjectId(client.getInstitutionPatientId(patient));
    dailyProcess.setSubjectPatcom(client.getEncounterIdentifier(encounter));
    Optional<String> room = client.getPatientRoom(encounter);
    if (room.isPresent()) {
      dailyProcess.setRoomNumber(room.get());
    }
  }

  /**
   * Populate the daily process data with observation data.
   *
   * @param dailyProcess
   *     daily process data
   * @param encounter
   *     the current patient encounter resource
   */
  private static void addObservationData(DailyProcess dailyProcess, Encounter encounter) {
    // Get all Observation resources for this encounter
    List<Observation> observations = client.getObservations(encounter);
    log.info("number of observations: {}", observations.size());

    // Process CAM-ICU Result
    CamIcuResult.processCamIcuResult(client, dailyProcess, observations);
  }

  /**
   * Populate the daily process data with medication data.
   *
   * @param dailyProcess
   *     daily process data
   * @param encounter
   *     the current patient encounter resource
   */
  private static void addMedicationPrescriptionData(
      DailyProcess dailyProcess, Encounter encounter) {
    // Get all MedicationPrescription resources for this encounter
    List<MedicationOrder> medicationOrders = client.getMedicationOrders(encounter);
    log.info("number of medication orders: {}", medicationOrders.size());
    for (MedicationOrder medicationOrder : medicationOrders) {
      Optional<String> medicationName = client.getMedicationName(medicationOrder);
      Optional<String> status;
      if (medicationName.isPresent()) {
        switch (medicationName.get()) {
          case "Continuous Infusion Lorazepam IV":
            status = client.getMedicationOrderStatus(medicationOrder);
            if (status.isPresent() && status.get().equals("active")) {
              dailyProcess.setContinuousInfusionLorazepam("Yes");
            }
            break;

          case "Continuous Infusion Midazolam IV":
            status = client.getMedicationOrderStatus(medicationOrder);
            if (status.isPresent() && status.get().equals("active")) {
              dailyProcess.setContinuousInfusionMidazolam("Yes");
            }
            break;

          case "Intermittent Chlordiazepoxide Enteral":
            status = client.getMedicationOrderStatus(medicationOrder);
            if (status.isPresent() && status.get().equals("active")) {
              dailyProcess.setIntermittentChlordiazepoxide("Yes");
            }
            break;

          case "Intermittent Lorazepam IV":
            status = client.getMedicationOrderStatus(medicationOrder);
            if (status.isPresent() && status.get().equals("active")) {
              dailyProcess.setIntermittentLorazepamIv("Yes");
            }
            break;

          case "Intermittent Lorazepam Enteral":
            status = client.getMedicationOrderStatus(medicationOrder);
            if (status.isPresent() && status.get().equals("active")) {
              dailyProcess.setIntermittentLorazepamPo("Yes");
            }
            break;

          case "Intermittent Midazolam IV":
            status = client.getMedicationOrderStatus(medicationOrder);
            if (status.isPresent() && status.get().equals("active")) {
              dailyProcess.setIntermittentMidazolamIv("Yes");
            }
            break;

          default:
            // Ignore other prescriptions
        }
      }
    }
  }
}
