// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureStatusEnum;
import com.datafascia.emerge.ucsf.codes.ProcedureRequestCodeEnum;
import java.util.List;

/**
 * Procedure helper methods
 */
public class ProcedureUtils {

  // Private constructor disallows creating instances of this class.
  private ProcedureUtils() {
  }

  /**
   * Checks if there is an active central line.
   *
   * @param procedures
   *     procedures to search
   * @return {@code true} if there is an active central line
   */
  public static boolean haveActiveLine(List<Procedure> procedures) {
    return procedures.stream().anyMatch(
        procedure -> procedure.getStatusElement().getValueAsEnum()
            == ProcedureStatusEnum.IN_PROGRESS);
  }

  /**
   * Finds freshest hypothermia blanket order.
   *
   * @param procedureRequests
   *     items to search
   * @return freshest hypothermia blanket order, or {@code null} if not found
   */
  public static ProcedureRequest findFreshestHypothermiaBlanketOrder(
      List<ProcedureRequest> procedureRequests) {

    procedureRequests.sort(ProcedureRequestUtils.getScheduledComparator().reversed());

    for (ProcedureRequest procedureRequest : procedureRequests) {
      String code = procedureRequest.getCode().getCodingFirstRep().getCode();
      if (code.equals(ProcedureRequestCodeEnum.HYPOTHERMIA_BLANKET_ORDER_1.getCode()) ||
          code.equals(ProcedureRequestCodeEnum.HYPOTHERMIA_BLANKET_ORDER_2.getCode())) {
        return procedureRequest;
      }
    }

    return null;
  }

  private static ProcedureRequest filterCodeFreshest(
            List<ProcedureRequest> procedureRequests, String code) {

    return procedureRequests.stream()
        .filter(request -> request.getCode().getCodingFirstRep().getCode().equals(code))
        .max(ProcedureRequestUtils.getScheduledComparator())
        .orElse(null);
  }

  private static ProcedureRequest filterIdentifierFreshest(
            List<ProcedureRequest> procedureRequests, String identifier) {

    return procedureRequests.stream()
        .filter(request -> request.getIdentifierFirstRep().getValue().equals(identifier))
        .max(ProcedureRequestUtils.getScheduledComparator())
        .orElse(null);
  }

  /**
   * Finds freshest place SCDs.
   *
   * @param procedureRequests
   *     items to search
   * @return freshest place SCDs, or {@code null} if not found
   */
  public static ProcedureRequest findFreshestPlaceSCDs(
      List<ProcedureRequest> procedureRequests) {
    return filterCodeFreshest(procedureRequests,
        ProcedureRequestCodeEnum.PLACE_SCDS.getCode());
  }

  /**
   * Finds freshest maintain SCDs.
   *
   * @param procedureRequests
   *     items to search
   * @return freshest maintain SCDs, or {@code null} if not found
   */
  public static ProcedureRequest findFreshestMaintainSCDs(
      List<ProcedureRequest> procedureRequests) {

    return filterCodeFreshest(procedureRequests,
        ProcedureRequestCodeEnum.MAINTAIN_SCDS.getCode());
  }

  /**
   * Finds freshest remove SCDs.
   *
   * @param procedureRequests
   *     items to search
   * @return freshest remove SCDs, or {@code null} if not found
   */
  public static ProcedureRequest findFreshestRemoveSCDs(List<ProcedureRequest> procedureRequests) {
    return filterCodeFreshest(procedureRequests,
        ProcedureRequestCodeEnum.REMOVE_SCDS.getCode());
  }
}
