// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import java.util.Comparator;
import java.util.Date;

/**
 * Comparator for sorting observations by effective date time in descending order.
 */
public class ObservationEffectiveDescendingComparator implements Comparator<Observation> {

  private static int compare(Date left, Date right) {
    if (left == null && right == null) {
      return 0;
    } else if (left == null) {
      return 1;
    } else if (right == null) {
      return -1;
    }

    return right.compareTo(left);
  }

  @Override
  public int compare(Observation left, Observation right) {
    if (left == null && right == null) {
      return 0;
    } else if (left == null) {
      return 1;
    } else if (right == null) {
      return -1;
    }

    return compare(
        ObservationUtils.getEffectiveDate(left), ObservationUtils.getEffectiveDate(right));
  }
}
