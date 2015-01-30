// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.csv.CSVMapper;
import com.datafascia.emerge.models.Demographic;
import com.datafascia.models.Encounter;
import com.datafascia.models.Hospitalization;
import com.datafascia.models.Name;
import com.datafascia.models.Observation;
import com.datafascia.models.Patient;
import com.google.common.base.Joiner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * The main application class for the Emerge CSV generator.
 */
@Slf4j
public class EmergeDemographicCSVGenerator {

  // Date format the CSV is expecting.
  private static final DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
  // Name format
  private static final String NAME_FMT
          = "<[" + Name.FIRST + "," + Name.MIDDLE + "," + Name.LAST + "]; separator=\" \">";

  private static final String HEIGHT = "Height";
  private static final String WEIGHT = "Weight";
  private static final String YES = "Yes";
  private static final String NO = "No";
  private static final String UNKNOWN = "Unknown";

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
   */
  public static void generate(URI apiEndpoint, String user, String password, String csvFile)
      throws IOException {

    mapper = new CSVMapper<>(Demographic.class);

    try (PrintWriter pw = new PrintWriter(new FileWriter(csvFile))) {
      DatafasciaApi api = DatafasciaApiBuilder.endpoint(apiEndpoint, user, password);
      pw.write(Joiner.on(",").join(mapper.getHeaders()));

      int entry = 0;
      for (Patient pat : api.patients()) {
        try {
          String[] urnparts = pat.getId().toString().split(":");
          String patientId = urnparts[urnparts.length - 1];
          Encounter encount = api.lastvisit(patientId);

          pw.write("\n" + mapper.asCSV(getDemographic(pat, encount, entry)));
          entry++;
        } catch (MissingUrnException ex) {
          log.error("Error processing patient: ", ex);
        }
      }
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
  private static Demographic getDemographic(Patient pat, Encounter encount, int entry)
          throws MissingUrnException {
    if (pat.getInstitutionPatientId() == null) {
      throw new MissingUrnException("Institution patient ID missing " + pat.getId().getRawPath());
    }

    Demographic demo = new Demographic();
    demo.setHighestLevelActivity(UNKNOWN);
    demo.setIvcFilter(NO);
    demo.setPriorToHospitalStay(UNKNOWN);
    demo.setReadmission(NO);
    demo.setScreeningToolUsed(YES);
    demo.setEntry(Integer.toString(entry));
    demo.setSubjectPatcom(Integer.toString(entry));

    addPatientData(demo, pat);

    if (encount != null) {
      addEncounterData(demo, encount);
    }

    // These are all unused, but still defined in the CSV.
    demo.setIpAddress(null);
    demo.setJhedId(null);
    demo.setDataCollectionDate(null);
    demo.setDateCreated(null);
    demo.setDateUpdated(null);

    return demo;
  }

  /**
   * Populate the demographic data with the patient object.
   *
   * @param demo
   * @param pat
   */
  private static void addPatientData(Demographic demo, Patient pat) {
    demo.setGender(pat.getGender().name());
    demo.setPatientDateOfBirth(df.format(pat.getBirthDate()));
    demo.setRace(pat.getRace().name());
    demo.setSubjectPatientId(getIdFromUrn(pat.getInstitutionPatientId()));
    demo.setPatientName(pat.getName().format(NAME_FMT));
  }

  /**
   * Populate the demographic data with the encounter object.
   *
   * @param demo
   * @param encount
   */
  private static void addEncounterData(Demographic demo, Encounter encount) {
    Hospitalization hosp = encount.getHospitalisation();
    if (hosp != null && hosp.getPeriod() != null && hosp.getPeriod().getStart() != null) {
      Date admitDate = hosp.getPeriod().getStart();
      demo.setSicuAdmissionDate(df.format(admitDate));
    }

    Optional<String> height = getObservationValue(encount, HEIGHT);
    if (height.isPresent()) {
      // UCSF stores height in inches, but the CSV expects centimeters.
      Double dheight = Double.parseDouble(height.get()) * 2.54;
      demo.setPatientAdmissionHeightCm(dheight.toString());
    }

    Optional<String> weight = getObservationValue(encount, WEIGHT);
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

  /**
   * Pull an ID out of a URN.
   *
   * @param urn The URN.
   *
   * @return The ID from the end.
   */
  private static String getIdFromUrn(URI urn) {
    if (urn == null) {
      return null;
    }

    String[] path = urn.toString().split(":");
    return path[path.length - 1];
  }

  @SuppressWarnings("serial")
  private static class MissingUrnException extends Exception {

    public MissingUrnException(String message) {
      super(message);
    }
  }
}