// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.util.Comparator;
import java.util.Date;

/**
 * Compares the effective time property of medication administration resources.
 */
public class MedicationAdministrationEffectiveTimeComparator implements
    Comparator<MedicationAdministration> {

  private static Date toDate(IDatatype value) {
    return ((DateTimeDt) value).getValue();
  }

  @Override
  public int compare(MedicationAdministration left, MedicationAdministration right) {
    return toDate(left.getEffectiveTime()).compareTo(toDate(right.getEffectiveTime()));
  }
}
