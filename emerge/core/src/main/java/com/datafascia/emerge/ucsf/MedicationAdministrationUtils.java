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
import java.util.List;

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
   * Returns false if there exists a MedicationAdministration for a given encounter with
   *   - Status: In Progress
   *   - Dosage.quantity > 0
   *   - Identifier that is present in a list of known Continuous MedSets, except PCAs
   *
   * Otherwise returns true.
   *
   * @param encounterId
   *    encounter to search for medication administrations
   * @param client
   *    API client
   * @param medsSet
   *    Meds group name that UCSF uses.
   * @return
   *    True if there is not a medicationAdministration that matches criteria for in progress
   *    infusion.
   */
  public static boolean notInfusing(ClientBuilder client, String encounterId, String medsSet) {
    return !activelyInfusing(client, encounterId, medsSet);
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
   * Returns true if there exists a MedicationAdministration for a given encounter for
   * a given Meds Set name with
   *   - Status: In Progress or Completed
   *   - Dosage.quantity > 0
   *   - Reason Not Given is null
   *   - Inside a given time frame.
   *
   * @param encounterId
   *    encounter to search for medication administrations
   * @param client
   *    API client
   * @param timeFrame
   *    Time window constraint for search.
   * @param medsSet
   *    Meds group name that UCSF uses.
   * @return
   *    True if there is a medicationAdministration that matches criteria for in progress
   *    infusion.
   */
  public static boolean beenAdministered(ClientBuilder client, String encounterId,
      PeriodDt timeFrame, String medsSet) {
    List<MedicationAdministration> medicationAdministrations = client.
        getMedicationAdministrationClient().search(encounterId);
    medicationAdministrations.addAll(client.getMedicationAdministrationClient()
        .search(encounterId));
    return medicationAdministrations.stream()
        .filter(medicationAdministration -> medicationAdministration.getStatusElement().
            getValueAsEnum().equals(MedicationAdministrationStatusEnum.IN_PROGRESS)
            || medicationAdministration.getStatusElement().getValueAsEnum()
            .equals(MedicationAdministrationStatusEnum.COMPLETED))
        .filter(admin -> admin.getIdentifierFirstRep().getValue().equals(medsSet))
        .filter(medicationAdministration -> dosageOverZero(medicationAdministration))
        .filter(medicationAdministration -> insideTimeFrame(medicationAdministration, timeFrame))
        .anyMatch(medicationAdministration -> dosageOverZero(medicationAdministration));
  }

  /**
   * Returns true if there exists a MedicationAdministration for a given encounter for
   * a given Meds Set name with
   *   - Status: In Progress or Completed
   *   - Inside a given time frame.
   *
   * @param encounterId
   *    encounter to search for medication administrations
   * @param client
   *    API client
   * @param timeFrame
   *    Time window constraint for search.
   * @param medsSet
   *    Meds group name that UCSF uses.
   * @return
   *    True if there is a medicationAdministration that matches criteria for in progress
   *    or completed administration.
   */
  public static boolean inProgressOrCompletedInTimeFrame(ClientBuilder client, String encounterId,
      PeriodDt timeFrame, String medsSet) {
    List<MedicationAdministration> medicationAdministrations = client.
        getMedicationAdministrationClient().search(encounterId);
    medicationAdministrations.addAll(client.getMedicationAdministrationClient()
        .search(encounterId));
    return medicationAdministrations.stream()
        .filter(medicationAdministration -> medicationAdministration.getStatusElement().
            getValueAsEnum().equals(MedicationAdministrationStatusEnum.IN_PROGRESS)
            || medicationAdministration.getStatusElement().getValueAsEnum()
            .equals(MedicationAdministrationStatusEnum.COMPLETED))
        .filter(admin -> admin.getIdentifierFirstRep().getValue().equals(medsSet))
        .anyMatch(medicationAdministration -> insideTimeFrame(medicationAdministration, timeFrame));
  }

  /**
   * Returns true if there exists a MedicationAdministration for a given encounter for
   * a given Meds Set name with
   *   - Status: In Progress
   *   - Inside a given time frame.
   *
   * @param encounterId
   *    encounter to search for medication administrations
   * @param client
   *    API client
   * @param timeFrame
   *    Time window constraint for search.
   * @param medsSet
   *    Meds group name that UCSF uses.
   * @return
   *    True if there is a medicationAdministration that matches criteria for in progress
   *    administration.
   */
  public static boolean inProgressInTimeFrame(ClientBuilder client, String encounterId,
      PeriodDt timeFrame, String medsSet) {
    List<MedicationAdministration> medicationAdministrations = client.
        getMedicationAdministrationClient().search(encounterId);
    medicationAdministrations.addAll(client.getMedicationAdministrationClient()
        .search(encounterId));
    return medicationAdministrations.stream()
        .filter(medicationAdministration -> medicationAdministration.getStatusElement().
            getValueAsEnum().equals(MedicationAdministrationStatusEnum.IN_PROGRESS))
        .filter(admin -> admin.getIdentifierFirstRep().getValue().equals(medsSet))
        .anyMatch(medicationAdministration -> insideTimeFrame(medicationAdministration, timeFrame));
  }

  /**
   * Returns true if a specified medication administration is inside a specified time window.
   *
   * @param admin
   *     Medication administration resource.
   * @param timeFrame
   *     Time window constraint for search.
   * @return
   *     True if the supplied administration is inside the specified time window.
   */
  public static boolean insideTimeFrame(MedicationAdministration admin, PeriodDt timeFrame) {
    IDatatype effectiveTime = admin.getEffectiveTime();
    if (effectiveTime instanceof TimingDt) {
      return ((TimingDt) effectiveTime).getEventFirstRep().getValue().after(timeFrame.getStart())
          && ((TimingDt) effectiveTime).getEventFirstRep().getValue().before(timeFrame.getEnd());
    } else if (effectiveTime instanceof PeriodDt) {
      return ((PeriodDt) effectiveTime).getStart().after(timeFrame.getStart())
          && ((PeriodDt) effectiveTime).getEnd().before(timeFrame.getEnd());
    } else if (effectiveTime instanceof DateTimeDt) {
      return ((DateTimeDt) effectiveTime).getValue().after(timeFrame.getStart())
          && ((DateTimeDt) effectiveTime).getValue().before(timeFrame.getEnd());
    } else {
      throw new RuntimeException("Unexpected type: " + effectiveTime.getClass().getCanonicalName());
    }
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
