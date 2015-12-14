// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.rass;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
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
   * Checks if observation is relevant to Target RASS.
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

    if (ProcedureRequestUtils.isCurrent(targetRass, Date.from(Instant.now(clock)))) {
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

      result.setDataEntryTime(targetRass.getOrderedOn());
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
    Optional<ProcedureRequest> targetRass = apiClient.getProcedureRequestClient()
        .list(encounterId)
        .stream()
        .filter(request -> ProcedureRequestCodeEnum.TARGET_RASS.isCodeEquals(request.getCode()))
        .max(ProcedureRequestUtils.getScheduledComparator());
    if (targetRass.isPresent()) {
      return targetRass.get();
    } else {
      return null;
    }
  }
}
