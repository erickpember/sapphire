// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import au.com.bytecode.opencsv.CSVReader;
import com.datafascia.api.services.ApiIT;
import java.io.FileReader;
import java.net.URI;
import java.util.HashMap;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * A test to ensure functionality of the CSV generation command.
 * <p>
 * We're using expected values here rather than pulling patients from the API because the CSV
 * contains transformed data. While the same transforms could be called here as in the CSV
 * generator, that would mean that mistakes in the transformation would happen the same way
 * in both places, and the test would erroneously pass. Here we have known good data, and
 * breaking changes in the transforms will cause a test failure.
 *
 * This integration test has been disabled from running, pending updates to the new API.
 * To enable this class, simply rename it back to CsvIT.
 */
public class CsvITDisabled {

  private static final String DEMOGRAPHIC_CSV_FILE = "/tmp/datafascia-test/emerge-demographic.csv";
  private static final String DAILY_PROCESS_CSV_FILE =
      "/tmp/datafascia-test/emerge-daily-process.csv";

  @SuppressWarnings("serial")
  HashMap<String, String[]> expectedPatients = new HashMap<String, String[]>() {
    {
      put("urn:df-institution-patientId-1:UCSF::96087004", new String[] {
          "AccountNumber1",
          "ECMNOTES TEST",
          "1977-01-01",
          "Female",
          "1002-5"
      });
      put("urn:df-institution-patientId-1:UCSF::96087039", new String[] {
          "AccountNumber2",
          "ONE A ECM-MSSGE",
          "1960-06-06",
          "Female",
          "2028-9"
      });
      put("urn:df-institution-patientId-1:UCSF::96087055", new String[] {
          "AccountNumber4",
          "ONE C ECM-MSSGE",
          "1996-07-29",
          "Male",
          "2131-1"
      });
      put("urn:df-institution-patientId-1:UCSF::96087063", new String[] {
          "AccountNumber5",
          "ONE D ECM-MSSGE",
          "1977-10-29",
          "Male",
          "2106-3"
      });
      put("urn:df-institution-patientId-1:UCSF:SICU:96087047", new String[] {
          "AccountNumber3",
          "ONE B ECM-MSSGE",
          "1954-10-29",
          "Female",
          "2054-5"
      });
      put("urn:df-institution-patientId-1:UCSF:SICU:97534012", new String[] {
          "AccountNumber6",
          "ONE C MB-CHILD",
          "1999-02-20",
          "Male",
          "2076-8"
      });
    }
  };

  HashMap<String, String> expectedDailyPatients = new HashMap<String, String>() {
    {
      put("urn:df-institution-patientId-1:UCSF::96087004", new String("AccountNumber1"));
      put("urn:df-institution-patientId-1:UCSF::96087039", new String("AccountNumber2"));
      put("urn:df-institution-patientId-1:UCSF::96087055", new String("AccountNumber4"));
      put("urn:df-institution-patientId-1:UCSF::96087063", new String("AccountNumber5"));
      put("urn:df-institution-patientId-1:UCSF:SICU:96087047", new String("AccountNumber3"));
      put("urn:df-institution-patientId-1:UCSF:SICU:97534012", new String("AccountNumber6"));
    }
  };

  @Test
  public void testDemographicCsv() throws Exception {
    EmergeDemographicCSVGenerator.generate(
        new URI("http://localhost:" + ApiIT.app.getLocalPort()),
        "testuser",
        "supersecret",
        DEMOGRAPHIC_CSV_FILE);

    CSVReader reader = new CSVReader(new FileReader(DEMOGRAPHIC_CSV_FILE));
    String[] line;
    while ((line = reader.readNext()) != null) {
      if (line[0].equals("Entry #")) {
        // Ignore the CSV's header.
        continue;
      }
      String[] csvPatient = expectedPatients.remove(line[6]);
      if (csvPatient == null) {
        fail("Patient " + line[6] + " in CSV, but not in the expected result set.");
      }

      assertEquals(csvPatient[0], line[7]);
      assertEquals(csvPatient[1], line[8]);
      assertEquals(csvPatient[2], line[11]);
      assertEquals(csvPatient[3], line[12]);
      assertEquals(csvPatient[4], line[13]);
    }

    assertEquals(expectedPatients.size(), 0);
  }

  @Test
  public void testDailyProcessCsv() throws Exception {
    EmergeDailyProcessCSVGenerator.generate(
        new URI("http://localhost:" + ApiIT.app.getLocalPort()),
        "testuser",
        "supersecret",
        DAILY_PROCESS_CSV_FILE);

    CSVReader reader = new CSVReader(new FileReader(DAILY_PROCESS_CSV_FILE));
    String[] line;
    while ((line = reader.readNext()) != null) {
      if (line[0].equals("Entry #")) {
        // Ignore the CSV's header.
        continue;
      }
      String csvPatient = expectedDailyPatients.remove(line[6]);
      if (csvPatient == null) {
        fail("Patient " + line[6] + " in CSV, but not in the expected result set.");
      }
      assertEquals(csvPatient, line[7]);
    }

    assertEquals(expectedDailyPatients.size(), 0);
  }
}