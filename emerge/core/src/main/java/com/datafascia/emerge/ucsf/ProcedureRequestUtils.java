// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import java.util.List;

/**
 * ProcedureRequest helper methods
 */
public class ProcedureRequestUtils {

  // Private constructor disallows creating instances of this class.
  private ProcedureRequestUtils() {
  }

  /**
   * Finds freshest ProcedureRequest.
   *
   * @param procedureRequests
   *     ProcedureRequests to search
   * @return freshest procedureRequest, or {@code null} if input procedureRequests is empty
   */
  public static ProcedureRequest findFreshestProcedureRequest(
      List<ProcedureRequest> procedureRequests) {
    return procedureRequests.stream()
        .max(new ProcedureRequestScheduledComparator())
        .orElse(null);
  }
}
