// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import com.beust.jcommander.JCommander;
import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.csv.CSVMapper;
import com.datafascia.emerge.models.Demographic;
import com.datafascia.models.Name;
import com.datafascia.models.Patient;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.common.base.Joiner;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import lombok.extern.slf4j.Slf4j;

/**
 * The main application class for the Emerge CSV generator.
 */
@Slf4j
public class EmergeCSV {
  // Date format the CSV is expecting.
  private static final DateFormat df  = new SimpleDateFormat("yyyy-MM-dd");
  // Name format
  private static final String NAME_FMT =
    "<[" + Name.FIRST + "," + Name.MIDDLE + "," + Name.LAST + "]; separator=\" \">";

  private static CSVMapper<Demographic> mapper;

  /**
   * The main entry point for the application
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) throws JsonProcessingException, IOException {
    EmergeCSVOpts opts = new EmergeCSVOpts();
    new JCommander(opts, args);
    mapper = new CSVMapper<>(Demographic.class);

    try (PrintWriter pw = new PrintWriter(new FileWriter(opts.csvFile))) {
      DatafasciaApi api = DatafasciaApiBuilder.endpoint(opts.apiEndpoint, opts.user, opts.password);
      pw.write(Joiner.on(",").join(mapper.getHeaders()));

      int entry = 0;
      for (Patient pat : api.patients()) {
        try {
          pw.write("\n" + mapper.asCSV(getDemographic(pat, entry)));
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
  private static Demographic getDemographic(Patient pat, int entry) throws MissingUrnException {
    if(pat.getInstitutionPatientId() == null){
      throw new MissingUrnException("Institution patient ID missing " + pat.getId().getRawPath());
    }

    Demographic demo = new Demographic();
    demo.setGender(pat.getGender().name());
    demo.setHighestLevelActivity("Unknown");
    demo.setIvcFilter("No");
    demo.setPatientDateOfBirth(df.format(pat.getBirthDate()));
    demo.setPatientName(pat.getName().format(NAME_FMT));
    demo.setPriorToHospitalStay("Unknown");
    demo.setRace(pat.getRace().name());
    demo.setReadmission("No");
    demo.setScreeningToolUsed("Yes");
    demo.setSubjectPatcom(Integer.toString(entry));
    demo.setSubjectPatientId(getIdFromUrn(pat.getInstitutionPatientId()));

    // Placeholders till Encounter dao is done
    demo.setSicuAdmissionDate("PLACEHOLDER");
    demo.setPatientAdmissionHeightCm("PLACEHOLDER");
    demo.setPatientAdmissionWeightKg("PLACEHOLDER");

    // These are all unused, but still defined in the CSV.
    demo.setIpAddress(null);
    demo.setJhedId(null);
    demo.setDataCollectionDate(null);
    demo.setDateCreated(null);
    demo.setDateUpdated(null);
    demo.setEntry(null);

    return demo;
  }

  /**
   * Pull an ID out of a URN.
   *
   * @param urn The URN.
   *
   * @return The ID from the end.
   */
  private static String getIdFromUrn(URI urn){
    String[] path = urn.toString().split(":");
    return path[path.length - 1];
  }

  @SuppressWarnings("serial")
  private static class MissingUrnException extends Exception {
    public MissingUrnException(String message){
      super(message);
    }
  }
}
