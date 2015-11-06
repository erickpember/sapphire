// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import com.datafascia.domain.fhir.Dates;
import java.util.Comparator;

/**
 * Compares the effective time property of medication administration resources.
 */
public class MedicationAdministrationEffectiveTimeComparator implements
    Comparator<MedicationAdministration> {

  @Override
  public int compare(MedicationAdministration left, MedicationAdministration right) {
    if ((left == null || left.getEffectiveTime() == null)
        && (right == null || right.getEffectiveTime() == null)) {
      return 0;
    } else if ((left == null || left.getEffectiveTime() == null)) {
      return -1;
    } else if ((right == null || right.getEffectiveTime() == null)) {
      return 1;
    }

    return Dates.toDate(left.getEffectiveTime()).compareTo(Dates.toDate(right.getEffectiveTime()));
  }
}
