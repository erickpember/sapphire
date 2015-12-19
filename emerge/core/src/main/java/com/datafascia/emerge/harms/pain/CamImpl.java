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
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Implements the pain and delirium harms CAM-ICU UTA utaReason and CAM-ICU result
 */
@Slf4j
public class CamImpl {

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  /**
   * Result container for Cam harms logic
   */
  @Data @Builder
  public static class CamImplResult {
    private String result;
    private String utaReason;
  }

  /**
   * Checks if observation is relevant to CAM-ICU.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to CAM-ICU.
   */
  public static boolean isRelevant(Observation observation) {
    return ObservationCodeEnum.CAM_ICU.isCodeEquals(observation.getCode());
  }

  /**
   * Implements the pain and delirium harms CAM-ICU UTA utaReason and CAM-ICU result
   *
   * @param encounterId
   *     encounter to check.
   * @return Cam UTA Reason and Result, or Optional.empty if not found.
   */
  public CamImplResult getCam(String encounterId) {
    PeriodDt fromCurrentOrPriorShift = ShiftUtils.getCurrentOrPriorShiftToNow(clock);

    Observation freshestFromShift = ObservationUtils.getFreshestByCodeInTimeFrame(apiClient,
        encounterId, ObservationCodeEnum.CAM_ICU.getCode(), fromCurrentOrPriorShift);

    String freshestValueFromShift = ObservationUtils.getValueAsString(freshestFromShift);

    return CamImplResult
        .builder()
        .utaReason(getCamUtaReason(freshestValueFromShift))
        .result(getCamResult(freshestValueFromShift))
        .build();
  }

  /**
   * Gets the pain and delirium CAM-ICU UTA Reason
   *
   * @param freshestCamValue
   *     Value of the freshest CAM-ICU coded observation in the shift.
   * @return Cam UTA Reason, or {@code null} if not found.
   */
  public String getCamUtaReason(String freshestCamValue) {
    if (freshestCamValue != null) {
      switch (freshestCamValue) {
        case "UTA (RASS -4 or -5)":
          return "RASS Score -4 or -5";
        case "UTA (Language barrier)":
          return "Language Barrier";
        case "UTA (Developmental delay)":
          return "Developmental Delay";
        // Expected values for Cam Result, don't need to log a warning about these.
        case "+":
        case "-":
          return null;
        default:
          log.warn("Unexpected CAM-ICU Observation value [{}] found", freshestCamValue);
      }
    }

    return null;
  }

  /**
   * Gets the pain and delirium CAM-ICU result.
   *
   * @param freshestCamValue
   *     Value of the freshest CAM-ICU coded observation in the shift.
   * @return CAM result
   */
  public String getCamResult(String freshestCamValue) {
    if (freshestCamValue != null) {
      switch (freshestCamValue) {
        case "+":
          return "Positive";
        case "-":
          return "Negative";
        case "UTA (RASS -4 or -5)":
        case "UTA (Language barrier)":
        case "UTA (Developmental delay)":
          return "UTA";
        default:
          log.warn("Unexpected CAM-ICU Observation value [{}] found", freshestCamValue);
      }
    }

    return "Not Completed";
  }
}
