// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.emerge.models.DailyProcess;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;

/**
 * Processes Observations for CAM-ICU Results
 */
@Slf4j
public class CamIcuResult {

  // Private constructor disallows creating instances of this class
  private CamIcuResult() {
  }

  /**
   * Processes all CAM-ICU observations
   *
   * @param client
   *     the FHIR client to use
   * @param dailyProcess
   *     the daily process object to fill
   * @param observations
   *     the list of observations to search
   */
  public static void processCamIcuResult(
      FhirClient client, DailyProcess dailyProcess, List<Observation> observations) {

    Date fromTime = Date.from(Instant.now().minus(13, ChronoUnit.HOURS));
    Date freshestTime = fromTime;
    String freshestValue = null;

    // Find the freshest CAM-ICU Result observation
    for (Observation observation: observations) {
      Optional<String> observationCode = client.getObservationCode(observation);
      if (observationCode.isPresent() && observationCode.get().equals("304890023")) {
        Optional<DateTimeDt> observationTime = client.getObservationTime(observation);
        if (observationTime.isPresent()) {
          log.info("observation time: {}", observationTime.get().getValueAsString());
          Optional<String> value = client.getObservationStringValue(observation);
          if (value.isPresent() && !freshestTime.after(observationTime.get().getValue())) {
            String myValue = value.get();
            if (myValue.equals("+") || myValue.equals("-") ||
                myValue.equals("UTA (RASS -4 or -5)") ||
                myValue.equals("UTA (Language barrier)") ||
                myValue.equals("UTA (Developmental delay)")) {
              freshestTime = observationTime.get().getValue();
              freshestValue = value.get();
            }
          }
        }
      }
    }

    if (freshestValue != null) {
      switch (freshestValue) {
        case "+":
          dailyProcess.setCamIcuResult("Positive");
          break;
        case "-":
          dailyProcess.setCamIcuResult("Negative");
          break;
        case "UTA (RASS -4 or -5)":
          dailyProcess.setCamIcuResult("UTA: RASS -4 or -5");
          break;
        case "UTA (Language barrier)":
          dailyProcess.setCamIcuResult("UTA: Language Barrier");
          break;
        case "UTA (Developmental delay)":
          dailyProcess.setCamIcuResult("UTA: Developmental Delay");
          break;
        default:
          dailyProcess.setCamIcuResult("Not Completed");
          break;
      }
    }
  }
}
