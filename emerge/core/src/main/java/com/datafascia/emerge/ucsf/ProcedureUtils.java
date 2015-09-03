// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureStatusEnum;
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

    procedureRequests.sort(new ProcedureRequestScheduledComparator().reversed());

    for (ProcedureRequest procedureRequest : procedureRequests) {
      String code = procedureRequest.getType().getCodingFirstRep().getCode();
      switch (code) {
        case "Hypothermia Blanket Order #1":
        case "Hypothermia Blanket Order #2":
          return procedureRequest;
      }
    }

    return null;
  }
}
