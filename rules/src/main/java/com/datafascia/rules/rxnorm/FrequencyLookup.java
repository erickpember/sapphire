// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.rules.rxnorm;

import com.datafascia.common.jackson.CSVMapper;
import com.datafascia.rules.model.IntermittentFrequency;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import lombok.extern.slf4j.Slf4j;

/**
 * Holds a lookup table of Intermittent frequencies.
 */
@Slf4j
public class FrequencyLookup {
  private static final HashMap<String, IntermittentFrequency> frequencies = new HashMap<>();
  private static final String csvFile = "/lookups/frequencies.csv";
  private static final CSVMapper<IntermittentFrequency> mapper = new CSVMapper<>(
          IntermittentFrequency.class);

  static {
    ArrayList<String> csvs = new ArrayList<>();
    try {
      csvs = readFile(csvFile);
      for (String csv : csvs) {
        IntermittentFrequency freq = mapper.fromCSV(csv);
        frequencies.put(freq.getId(), freq);
      }
    } catch (IOException ex) {
      log.error("problem reading file: ", ex);
    }
  }

  /**
   * Returns true if the frequency lookup has a matching ID where Intermittent is true. Returns
   * false otherwise.
   *
   * @param id Frequency identifier.
   * @return True if a match is Intermittent.
   */
  public static boolean isIntermittent(String id) {
    if (frequencies.containsKey(id)) {
      return frequencies.get(id).isIntermittent();
    } else {
      return false;
    }
  }

  /**
   * Returns true if the frequency lookup has a matching ID where Daily is true. Returns false
   * otherwise.
   *
   * @param id Frequency identifier.
   * @return True if a match is Daily.
   */
  public static boolean isDaily(String id) {
    if (frequencies.containsKey(id)) {
      return frequencies.get(id).isDaily();
    } else {
      return false;
    }
  }

  /**
   * Returns true if the frequency lookup has a matching ID where Twice Daily is true. Returns false
   * otherwise.
   *
   * @param id Frequency identifier.
   * @return True if a match is Twice Daily.
   */
  public static boolean isTwiceDaily(String id) {
    if (frequencies.containsKey(id)) {
      return frequencies.get(id).isTwiceDaily();
    } else {
      return false;
    }
  }

  /**
   * Returns true if the frequency lookup has a matching ID where Once is true. Returns false
   * otherwise.
   *
   * @param id Frequency identifier.
   * @return True if a match is Once.
   */
  public static boolean isOnce(String id) {
    if (frequencies.containsKey(id)) {
      return frequencies.get(id).isOnce();
    } else {
      return false;
    }
  }

  /**
   * Returns true if the frequency lookup has a matching ID where Intermittent is false.
   *
   * @param id Frequency identifier.
   * @return True if a match is Continuous.
   */
  public static boolean isContinuous(String id) {
    if (frequencies.containsKey(id)) {
      return !frequencies.get(id).isIntermittent();
    } else {
      return false;
    }
  }

  /**
   * Read a file by line to a list of strings.
   *
   * @param filename Path of target file.
   * @return A string for each line, in a list.
   * @throws IOException Failure to read the file.
   */
  private static ArrayList<String> readFile(String filename)
          throws IOException {
    ArrayList<String> lines = new ArrayList<>();
    BufferedReader reader = new BufferedReader(new InputStreamReader(
                           FrequencyLookup.class.getClass().getResourceAsStream(filename)));

    String inputLine = null;

    while ((inputLine = reader.readLine()) != null) {
      lines.add(inputLine);
    }
    return lines;
  }
}