// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import au.com.bytecode.opencsv.CSVReader;
import com.datafascia.api.services.ApiTestSupport;
import java.io.FileReader;
import java.net.URI;
import org.testng.annotations.Test;

/**
 * A test to ensure functionality of the CSV generation command.
 */
public class CsvGeneratorIT extends ApiTestSupport {

  private static final String DEMOGRAPHIC_CSV_FILE =
      "/tmp/datafascia-test/emerge-demographic.csv";
  private static final String DAILY_PROCESS_CSV_FILE =
      "/tmp/datafascia-test/emerge-daily-process.csv";

  @Test
  public void testDemographicCsv() throws Exception {
    EmergeDemographicCSVGenerator.generate(
        "13I",
        new URI(API_ENDPOINT_URL),
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
    }
  }

  @Test
  public void testDailyProcessCsv() throws Exception {
    EmergeDailyProcessCSVGenerator.generate(
        "13I",
        new URI(API_ENDPOINT_URL),
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
    }
  }
}
