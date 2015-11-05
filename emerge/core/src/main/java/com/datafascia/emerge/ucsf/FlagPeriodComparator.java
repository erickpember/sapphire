// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares time period property of flags.
 */
public class FlagPeriodComparator implements Comparator<Flag> {

  private static Date toDate(IDatatype value) {
    return ((DateTimeDt) value).getValue();
  }

  @Override
  public int compare(Flag left, Flag right) {
    if ((left == null || left.getPeriod() == null)
        && (right == null || right.getPeriod() == null)) {
      return 0;
    } else if ((left == null || left.getPeriod() == null)) {
      return -1;
    } else if ((right == null || right.getPeriod() == null)) {
      return 1;
    }

    return toDate(left.getPeriod().getStartElement()).compareTo(toDate(right.getPeriod().
        getStartElement()));
  }
}
