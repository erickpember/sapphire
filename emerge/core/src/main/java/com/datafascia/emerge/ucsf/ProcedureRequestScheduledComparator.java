// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import com.datafascia.domain.fhir.Dates;
import java.util.Comparator;

/**
 * Compares scheduled date time property of procedure requests.
 */
public class ProcedureRequestScheduledComparator implements Comparator<ProcedureRequest> {

  @Override
  public int compare(ProcedureRequest left, ProcedureRequest right) {
    if ((left == null || left.getScheduled() == null)
        && (right == null || right.getScheduled() == null)) {
      return 0;
    } else if ((left == null || left.getScheduled() == null)) {
      return -1;
    } else if ((right == null || right.getScheduled() == null)) {
      return 1;
    }

    return Dates.toDate(left.getScheduled()).compareTo(Dates.toDate(right.getScheduled()));
  }
}
