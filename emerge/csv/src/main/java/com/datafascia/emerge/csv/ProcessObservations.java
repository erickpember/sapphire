// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.emerge.models.DailyProcess;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes all observations for an encounter and updates the Daily Process mapper
 */
@Slf4j
public class ProcessObservations {

  /**
   * Processes all applicable observations for the patient
   *
   * @param dailyProcess
   *     the daily process object mapper
   * @param observations
   *     the list of observations to process for this encounter
   */
  public void processObservations(DailyProcess dailyProcess, List<Observation> observations) {
    // Check each CSV column in turn against the observation entries
  }
}
