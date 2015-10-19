// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Pain and Delirium Harm Clinician RASS Goal Implementation
 */
public class RassGoalImpl {

  @Inject
  private static ClientBuilder apiClient;

  /**
   * Pain and Delirium Harm Clinician RASS Goal Implementation
   *
   * @param encounterId
   *     encounter to search
   * @return
   *     Most recent RASS goal, or 11 if absent.
   **/
  public static Integer getRassGoal(String encounterId) {
    ProcedureRequest targetRass = getTargetRass(encounterId);

    if (ProcedureRequestUtils.isCurrent(targetRass)) {
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_0_ALERT_AND_CALM
          .getCode())) {
        return 0;
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NEG1_DROWSY.getCode())) {
        return -1;
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NEG2_LIGHT_SEDATION.getCode())) {
        return -2;
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NEG2_MODERATE_SEDATION.getCode())) {
        return -3;
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NEG4_DEEP_SEDATION.getCode())) {
        return -4;
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NEG5_UNAROUSABLE
          .getCode())) {
        return -5;
      }
      if (targetRass.getNotesFirstRep().getText()
          .equals(ProcedureRequestCodeEnum.TARGET_RASS_NA_NMBA.getCode())) {
        return 12;
      }
      if (targetRass.getNotesFirstRep().getText().equals(
          ProcedureRequestCodeEnum.TARGET_RASS_NA_SEIZURES_STATUS_EPILEPTICUS.getCode())) {
        return 13;
      }
    }

    return 11;
  }

  /**
   * Pulls the procedure request for target RASS.
   *
   * @param encounterId
   *     relevant encounter for search
   * @return Procedure request with notes value "Target RASS" or null if not found.
   */
  private static ProcedureRequest getTargetRass(String encounterId) {
    Optional<ProcedureRequest> targetRass = apiClient.getProcedureRequestClient()
        .getProcedureRequest(encounterId).stream().filter(order -> order.getCode()
            .getText().equals("Target RASS")).findAny();
    if (targetRass.isPresent()) {
      return targetRass.get();
    } else {
      return null;
    }
  }
}