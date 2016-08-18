// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares the effective time property of medication administrations.
 */
public class MedicationAdministrationEffectiveTimeComparator implements
    Comparator<MedicationAdministration> {

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
  public int compare(MedicationAdministration left, MedicationAdministration right) {
    if (left == null && right == null) {
      return 0;
    } else if (left == null) {
      return -1;
    } else if (right == null) {
      return 1;
    }

    return compare(MedicationAdministrationUtils.getEffectiveTime(left),
        MedicationAdministrationUtils.getEffectiveTime(right));
  }
}
