// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.resource.ProcedureRequest;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares scheduled date time property of procedure requests.
 */
public class ProcedureRequestScheduledComparator implements Comparator<ProcedureRequest> {

  private static Date toDate(IDatatype value) {
    return ((DateTimeDt) value).getValue();
  }

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

    return toDate(left.getScheduled()).compareTo(toDate(right.getScheduled()));
  }
}
