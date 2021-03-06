// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.common.inject.Injectors;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.Periods;
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
   * Checks if observation is relevant to CAM-ICU and within the necessary time window.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to CAM-ICU.
   */
  public static boolean isRelevant(Observation observation) {
    Clock clock = Injectors.getInjector().getInstance(Clock.class);
    return ObservationUtils.isAfter(
        observation, Periods.getCurrentOrPriorShiftToNow(clock).getStart()) &&
        ObservationCodeEnum.CAM_ICU.isCodeEquals(observation.getCode());
  }

  /**
   * Implements the pain and delirium harms CAM-ICU UTA utaReason and CAM-ICU result
   *
   * @param encounterId
   *     encounter to check.
   * @return Cam UTA Reason and Result, or Optional.empty if not found.
   */
  public CamImplResult getCam(String encounterId) {
    PeriodDt fromCurrentOrPriorShift = Periods.getCurrentOrPriorShiftToNow(clock);

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
