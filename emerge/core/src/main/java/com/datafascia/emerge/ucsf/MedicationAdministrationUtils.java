// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import java.math.BigDecimal;
import java.util.Date;

/**
 * MedicationAdministration helper methods
 */
public class MedicationAdministrationUtils {
  // Private constructor disallows creating instances of this class.
  private MedicationAdministrationUtils() {
  }

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

  /**
   * Given an encounter and a Meds Set group name, return true if, of the administrations
   * for this encounter and meds set, the freshest administration has a status of in-progress
   * and a dosage over zero.
   *
   * @param encounterId
   *    encounter to search for medication administrations
   * @param client
   *    API client
   * @param medsSet
   *    Meds group name that UCSF uses.
   * @return
   *    True if there is a medicationAdministration that matches criteria for in progress
   *    infusion.
   */
  public static boolean activelyInfusing(ClientBuilder client, String encounterId, String medsSet) {
    return client.getMedicationAdministrationClient()
        .search(encounterId).stream()
        .filter(admin -> admin.getIdentifierFirstRep().getValue().equals(medsSet))
        .max(new MedicationAdministrationEffectiveTimeComparator())
        .filter(medicationAdministration -> medicationAdministration.getStatusElement().
            getValueAsEnum().equals(MedicationAdministrationStatusEnum.IN_PROGRESS))
        .filter(medicationAdministration -> dosageOverZero(medicationAdministration)).isPresent();
  }

  /**
   * Returns true if a specified medication administration has a nonzero dosage.
   *
   * @param admin
   *     Medication administration resource.
   * @return
   *     True if the supplied administration has a nonzero dosage.
   */
  public static boolean dosageOverZero(MedicationAdministration admin) {
    return admin.getDosage().getQuantity().getValue().compareTo(BigDecimal.ZERO) > 0;
  }
}
