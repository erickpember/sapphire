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
package com.datafascia.emerge.harms.rass;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureRequestStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Data;

/**
 * Pain and delirium clinician RASS goal implementation
 */
public class RassGoalImpl {

  /**
   * Result container for the pain and delirium RASS goal
   */
  @Data
  @Builder
  public static class RassGoalResult {
    private int goal;
    private Date dataEntryTime;
  }

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  /**
   * Checks if procedure request is relevant to Target RASS.
   *
   * @param request
   *     the procedureRequest to check
   * @return true if procedureRequest is relevant to Target RASS.
   */
  public static boolean isRelevant(ProcedureRequest request) {
    return ProcedureRequestCodeEnum.TARGET_RASS.isCodeEquals(request.getCode());
  }

  /**
   * Pain and delirium clinician RASS goal implementation
   *
   * @param encounterId
   *     encounter to search
   * @return freshest RASS goal
   **/
  public RassGoalResult getRassGoal(String encounterId) {
    RassGoalResult result = RassGoalResult.builder()
        .dataEntryTime(Date.from(Instant.now(clock)))
        .goal(11)
        .build();

    ProcedureRequest targetRass = getTargetRass(encounterId);

    if (targetRass != null) {
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_0_ALERT_AND_CALM
          .getCode())) {
        result.setGoal(0);
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NEG1_DROWSY.getCode())) {
        result.setGoal(-1);
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NEG2_LIGHT_SEDATION.getCode())) {
        result.setGoal(-2);
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NEG2_MODERATE_SEDATION.getCode())) {
        result.setGoal(-3);
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NEG4_DEEP_SEDATION.getCode())) {
        result.setGoal(-4);
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NEG5_UNAROUSABLE
          .getCode())) {
        result.setGoal(-5);
      }
      if (targetRass.getNotesFirstRep().getText()
          .equals(ProcedureRequestCodeEnum.TARGET_RASS_NA_NMBA.getCode())) {
        result.setGoal(12);
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NA_SEIZURES_STATUS_EPILEPTICUS.getCode())) {
        result.setGoal(13);
      }
    }

    return result;
  }

  /**
   * Pulls the procedure request for target RASS.
   *
   * @param encounterId
   *     relevant encounter for search
   * @return Procedure request with notes value "Target RASS" or null if not found.
   */
  private ProcedureRequest getTargetRass(String encounterId) {
    Optional<ProcedureRequest> inProgressTargetRass = apiClient.getProcedureRequestClient()
        .search(encounterId,
            ProcedureRequestCodeEnum.TARGET_RASS.getCode(),
            ProcedureRequestStatusEnum.IN_PROGRESS.getCode())
        .stream()
        .max(ProcedureRequestUtils.getScheduledComparator());
    if (inProgressTargetRass.isPresent()) {
      return inProgressTargetRass.get();
    } else {
      return null;
    }
  }
}
