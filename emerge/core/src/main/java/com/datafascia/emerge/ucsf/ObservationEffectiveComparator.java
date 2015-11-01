// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares effective date time property of observations.
 */
public class ObservationEffectiveComparator implements Comparator<Observation> {

  private static int compare(Date left, Date right) {
    if (left == null && right == null) {
      return 0;
    } else if (left == null) {
      return -1;
    } else if (right == null) {
      return 1;
    }

    return left.compareTo(right);
  }

  @Override
  public int compare(Observation left, Observation right) {
    return compare(
        ObservationUtils.getEffectiveDate(left), ObservationUtils.getEffectiveDate(right));
  }
}
