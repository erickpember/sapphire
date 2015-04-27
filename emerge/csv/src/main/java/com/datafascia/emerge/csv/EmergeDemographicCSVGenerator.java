// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.common.api.ApiParams;
import com.datafascia.common.jackson.CSVMapper;
import com.datafascia.common.persist.Id;
import com.datafascia.domain.model.Encounter;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.Name;
import com.datafascia.domain.model.Observation;
import com.datafascia.domain.model.PagedCollection;
import com.datafascia.domain.model.Patient;
import com.datafascia.emerge.models.Demographic;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import retrofit.RetrofitError;

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
  private static final String NAME_FMT
          = "<[" + Name.FIRST + "," + Name.MIDDLE + "," + Name.LAST + "]; separator=\" \">";

  private static final String HEIGHT = "Height";
  private static final String WEIGHT = "Weight";

  private static CSVMapper<Demographic> mapper;

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

    mapper = new CSVMapper<>(Demographic.class);

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

          pw.println(mapper.asCSV(getDemographic(pat, encount, entry)));
          entry++;
        }
        params = page.getNext();
      }
    }
  }

  /**
   * @return the last patient encounter information
   *
   * @param api the API end-point
   * @param patientId the patientId
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
   * Take a patient object and entry number, and return a populated Demographic object.
   *
   * @param pat patient object.
   * @param entry entry number (Patcome in the CSV)
   *
   * @return the Demographic object.
   */
  private static Demographic getDemographic(Patient pat, Encounter encount, int entry) {
    Demographic demo = new Demographic();
    demo.setEntry(Integer.toString(entry));

    LocalDateTime now = LocalDateTime.now(TIME_ZONE);
    demo.setDateCreated(DATE_TIME_FORMATTER.format(now));
    demo.setDataCollectionDate(DATE_FORMATTER.format(now));

    addPatientData(demo, pat);

    if (encount != null) {
      addEncounterData(demo, encount);
    }

    return demo;
  }

  /**
   * Populate the demographic data with the patient object.
   *
   * @param demo
   * @param pat
   */
  private static void addPatientData(Demographic demo, Patient pat) {
    demo.setGender(pat.getGender().equals(Gender.FEMALE) ? "Female" : "Male");
    demo.setPatientDateOfBirth(DATE_FORMATTER.format(pat.getBirthDate()));
    demo.setRace(pat.getRace().getCode());
    demo.setSubjectPatientId(pat.getInstitutionPatientId());
    demo.setSubjectPatcom(pat.getAccountNumber());
    demo.setPatientName(pat.getName().format(NAME_FMT));
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
    if (encounter.getPeriod() != null && encounter.getPeriod().getStartInclusive() != null) {
      ZonedDateTime admitTime = ZonedDateTime.ofInstant(
          encounter.getPeriod().getStartInclusive(), TIME_ZONE);
      demo.setSicuAdmissionDate(DATE_FORMATTER.format(admitTime));
    }

    Optional<String> height = getObservationValue(encounter, HEIGHT);
    if (height.isPresent()) {
      // UCSF stores height in inches, but the CSV expects centimeters.
      Double dheight = Double.parseDouble(height.get()) * 2.54;
      demo.setPatientAdmissionHeightCm(dheight.toString());
    }

    Optional<String> weight = getObservationValue(encounter, WEIGHT);
    if (weight.isPresent()) {
      demo.setPatientAdmissionWeightKg(weight.get());
    }
  }

  /**
   * Returns the value for a given observation by it's code.
   *
   * @param encount
   * @param code
   * @return
   */
  private static Optional<String> getObservationValue(Encounter encount, String code) {
    List<Observation> observations = encount.getObservations();
    if (observations != null) {
      for (Observation observ : observations) {
        if (observ.getName() != null && observ.getName().getCode() != null
                && observ.getName().getCode().equals(code)) {
          if (observ.getValues() != null && observ.getValues().getQuantity() != null) {
            return Optional.of(observ.getValues().getQuantity().getValue().toString());
          }
        }
      }
    }

    return Optional.empty();
  }

  private EmergeDemographicCSVGenerator() {
  }
}
