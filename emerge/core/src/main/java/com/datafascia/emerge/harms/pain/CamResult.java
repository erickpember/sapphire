// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.ShiftUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import javax.inject.Inject;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * Implements the pain and delirium harms CAM-ICU result
 */
@Slf4j
public class CamResult {

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private static Clock clock;

  /**
   * Checks if observation is relevant to CAM-ICU result.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to CAM level.
   */
  public static boolean isRelevant(Observation observation) {
    return ObservationCodeEnum.CAM_ICU.getCode().equals(observation.getCode().getCodingFirstRep()
        .getCode());
  }

  /**
   * Implements the pain and delirium CAM-ICU result
   *
   * @param encounterId
   *     encounter to check.
   * @return Cam Result, or "Not Documented" if not found.
   */
  public String getCamResult(String encounterId) {
    PeriodDt currentOrPriorShift = ShiftUtils.getCurrentOrPreviousShift(clock);

    Observation freshestFromShift = ObservationUtils.getFreshestByCodeInTimeFrame(apiClient,
        encounterId, ObservationCodeEnum.CAM_ICU.getCode(), currentOrPriorShift);

    if (freshestFromShift == null || Strings.isNullOrEmpty(ObservationUtils.getValueAsString(
        freshestFromShift))) {
      return "Not Documented";
    }

    switch (ObservationUtils.getValueAsString(freshestFromShift)) {
      case "+":
        return "Positive";
      case "-":
        return "Negative";
      case "UTA (RASS Score -4 or -5)":
      case "UTA (Language barrier)":
      case "UTA (Developmental delay)":
        return "UTA";
      default:
        log.warn("Unexpected Cam Result value:" + ObservationUtils.getValueAsString(
            freshestFromShift) + " found in observation " + freshestFromShift.getId().getValue());
        return "Not Documented";
    }
  }
}
