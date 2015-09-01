// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.Procedure;
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
}
