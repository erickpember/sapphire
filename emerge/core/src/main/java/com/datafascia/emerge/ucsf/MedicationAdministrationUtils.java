// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.util.Date;

/**
 * MedicationAdministration helper methods
 */
public class MedicationAdministrationUtils {
  /**
   * Returns true if a specified medication administration is after a specified time.
   *
   * @param admin
   *     Medication administration resource.
   * @param startTime
   *     Start time for search.
   * @return
   *     True if the supplied administration's effective time is after the specified start time.
   */
  public static boolean isAfter(MedicationAdministration admin, Date startTime) {
    IDatatype effectiveTime = admin.getEffectiveTime();
    if (effectiveTime instanceof TimingDt) {
      return ((TimingDt) effectiveTime).getEventFirstRep().getValue().after(startTime);
    } else if (effectiveTime instanceof PeriodDt) {
      return ((PeriodDt) effectiveTime).getStart().after(startTime);
    } else if (effectiveTime instanceof DateTimeDt) {
      return ((DateTimeDt) effectiveTime).getValue().after(startTime);
    } else {
      throw new RuntimeException("Unexpected type: " + effectiveTime.getClass().getCanonicalName());
    }
  }
}
