// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares the date written property of medication order resources.
 */
public class MedicationOrderDateWrittenComparator implements Comparator<MedicationOrder> {

  private static Date toDate(IDatatype value) {
    return ((DateTimeDt) value).getValue();
  }

  @Override
  public int compare(MedicationOrder left, MedicationOrder right) {
    if ((left == null || left.getDateWrittenElement() == null)
        && (right == null || right.getDateWrittenElement() == null)) {
      return 0;
    } else if ((left == null || left.getDateWrittenElement() == null)) {
      return -1;
    } else if ((right == null || right.getDateWrittenElement() == null)) {
      return 1;
    }

    return toDate(left.getDateWrittenElement()).compareTo(toDate(right.getDateWrittenElement()));
  }
}
