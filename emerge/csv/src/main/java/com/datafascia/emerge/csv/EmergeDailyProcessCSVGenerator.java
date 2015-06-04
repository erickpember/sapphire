// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.common.api.ApiParams;
import com.datafascia.common.jackson.CSVMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.PagedCollection;
import com.datafascia.domain.model.Patient;
import com.datafascia.emerge.models.DailyProcess;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import retrofit.RetrofitError;

/**
 * The main application class for the Emerge CSV generator.
 */
@Slf4j
public class EmergeDailyProcessCSVGenerator {

  private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");
  private static final DateTimeFormatter DATE_TIME_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
  // Date format the CSV is expecting.
  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

  private static CSVMapper<DailyProcess> mapper;

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
   * @throws IOException if error writing to CSV file
   */
  public static void generate(URI apiEndpoint, String user, String password, String csvFile)
      throws IOException {

    mapper = new CSVMapper<>(DailyProcess.class);

    try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
      DatafasciaApi api = DatafasciaApiBuilder.endpoint(apiEndpoint, user, password);
      pw.println(Joiner.on(",").join(mapper.getHeaders()));

      int entry = 0;
      Map<String, String> params = ImmutableMap.of(
          ApiParams.ACTIVE, "true", ApiParams.COUNT, "100");
      while (params != null) {
        PagedCollection<Patient> page = api.patients(params);
        for (Patient pat : page.getCollection()) {
          Encounter encount = lastVisit(api, pat.getId());

          pw.println(mapper.asCSV(getDailyProcess(pat, encount, entry)));
          entry++;
        }
        params = page.getNext();
      }
    }
  }

  /**
   * Gets last encounter for the patient.
   *
   * @param api
   *     the API end-point
   * @param patientId
   *     the patientId
   * @return the last patient encounter information
   */
  private static Encounter lastVisit(DatafasciaApi api, Id<Patient> patientId) {
    try {
      return api.lastvisit(patientId.toString());
    } catch (RetrofitError rf) {
      log.error("No last encounter found for patient {}", patientId);
      return null;
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
  private static DailyProcess getDailyProcess(Patient patient, Encounter encounter, int entry) {
    DailyProcess dailyProcess = new DailyProcess();
    dailyProcess.setEntry(Integer.toString(entry));

    LocalDateTime now = LocalDateTime.now(TIME_ZONE);
    dailyProcess.setDateCreated(DATE_TIME_FORMATTER.format(now));
    dailyProcess.setDataCollectionDate(DATE_FORMATTER.format(now));

    addPatientData(dailyProcess, patient);
    return dailyProcess;
  }

  /**
   * Populate the daily process data with the patient object.
   *
   * @param dailyProcess
   *     daily process data
   * @param patient
   *     patient
   */
  private static void addPatientData(DailyProcess dailyProcess, Patient patient) {
    dailyProcess.setSubjectId(patient.getInstitutionPatientId());
    dailyProcess.setSubjectPatcom(patient.getAccountNumber());
  }

  // Private constructor disallows creating instances of this class
  private EmergeDailyProcessCSVGenerator() {
  }
}
