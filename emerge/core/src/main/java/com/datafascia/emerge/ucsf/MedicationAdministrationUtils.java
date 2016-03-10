// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.CodingSystems;
import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * MedicationAdministration helper methods
 */
@Slf4j
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
      return ((TimingDt) effectiveTime).getEventFirstRep().getValue().compareTo(startTime) >= 0;
    } else if (effectiveTime instanceof PeriodDt) {
      return ((PeriodDt) effectiveTime).getStart().compareTo(startTime) >= 0;
    } else if (effectiveTime instanceof DateTimeDt) {
      return ((DateTimeDt) effectiveTime).getValue().compareTo(startTime) >= 0;
    } else {
      throw new RuntimeException("Unexpected type: " + effectiveTime.getClass().getCanonicalName());
    }
  }

  /**
   * Given one medication administration, return true if the administration
   * with a given meds set has a status of in-progress and a dosage over zero.
   * This method does not determine if the meds set entered is intermittent or continuous.
   *
   * @param admin
   *    A particular medication administration.
   * @param medsSet
   *    Meds group name that UCSF uses.
   * @param now
   *    The current time.
   * @param hoursAgo
   *    The duration of the look-back window, in hours.
   * @param apiClient
   *    The API client.
   * @param encounterId
   *    The identifier for the corresponding encounter.
   * @return
   *    True if there is a medicationAdministration that matches criteria for in progress
   *    infusion.
   */
  public static boolean isActivelyInfusing(MedicationAdministration admin,
      String medsSet, Instant now, Long hoursAgo, ClientBuilder apiClient, String encounterId) {
    return isActivelyInfusing(Collections.singleton(admin), medsSet, now, hoursAgo, apiClient,
        encounterId);
  }

  /**
   * Given a full list of med admins for an encounter, return true if the freshest administration
   * with a given meds set has a status of in-progress and a dosage over zero.
   * This method does not determine if the meds set entered is intermittent or continuous.
   *
   * @param allAdminsPerEncounter
   *    A full list of medication administrations for an encounter.
   * @param medsSet
   *    Meds group name that UCSF uses.
   * @param now
   *    The current time.
   * @param hoursAgo
   *    The duration of the look-back window, in hours.
   * @param apiClient
   *    The API client.
   * @param encounterId
   *    The identifier for the corresponding encounter.
   * @return
   *    True if there is a medicationAdministration that matches criteria for in progress
   *    infusion.
   */
  public static boolean isActivelyInfusing(
      Collection<MedicationAdministration> allAdminsPerEncounter,
      String medsSet, Instant now, Long hoursAgo, ClientBuilder apiClient, String encounterId) {
    allAdminsPerEncounter = freshestOfAllOrders(allAdminsPerEncounter).values();

    Date lowerTimeBound = Date.from(now.minus(hoursAgo, ChronoUnit.HOURS));

    log.debug(
        "isActivelyInfusing: encounter[{}], medsSet[{}], now[{}], hoursAgo[{}], lowerTimeBound[{}]",
        encounterId, medsSet, now, hoursAgo, lowerTimeBound);

    return allAdminsPerEncounter.stream()
        .filter(admin -> hasMedsSet(admin, medsSet))
        .filter(admin -> dosageOverZero(admin))
        .anyMatch(admin -> admin.getStatusElement().getValueAsEnum()
            == MedicationAdministrationStatusEnum.IN_PROGRESS &&
            (isAfter(admin, lowerTimeBound) ||
                orderIsActiveOrDraft(admin, apiClient, encounterId)));
  }

  /**
   * Return true if the medication order to which a given medication administration refers to
   * has a status of active or draft.
   *
   * @param admin
   *    The medication administration to check.
   * @param apiClient
   *    The API client.
   * @param encounterId
   *    The identifier for the corresponding encounter.
   * @return
   *    True if the medication order the administration refers to has a status of active or draft.
   */
  public static boolean orderIsActiveOrDraft(MedicationAdministration admin,
      ClientBuilder apiClient, String encounterId) {
    String orderId = admin.getPrescription().getReference().getIdPart();
    MedicationOrder order = apiClient.getMedicationOrderClient().read(orderId, encounterId);
    return MedicationOrderUtils.isActiveOrDraft(order);
  }

  /**
   * Given a list of medication administrations, this will get the freshest for each medication
   * order.
   *
   * @param admins A list of administrations.
   * @return A mapping of medication orders to administrations.
   */
  public static Map<String, MedicationAdministration>
      freshestOfAllOrders(Collection<MedicationAdministration> admins) {
    Map<String, MedicationAdministration> freshestMap = new HashMap<>();

    for (MedicationAdministration admin: admins) {
      String orderId = admin.getPrescription().getReference().getIdPart();

      MedicationAdministration freshestAdmin = freshestMap.get(orderId);
      if (freshestAdmin != null) {
        if (freshestAdmin.getEffectiveTime() != null
            && admin.getEffectiveTime() != null
            && getEffectiveDate(freshestAdmin).before(getEffectiveDate(admin))) {
          freshestMap.put(orderId, admin);
        }
      } else {
        freshestMap.put(orderId, admin);
      }
    }

    return freshestMap;
  }

  /**
   * Returns true if the latest administration of any order with a given Meds Set name with
   *   - Status: In Progress or Completed
   *   - Dosage.quantity > 0
   *   - Reason Not Given is null
   *   - Inside a given time frame.
   *
   * @param allAdminsPerEncounter
   *    A full list of medication administrations for an encounter.
   * @param timeFrame
   *    Time window constraint for search.
   * @param medsSet
   *    Meds group name that UCSF uses.
   * @return
   *    True if there is a medicationAdministration that matches criteria for in progress
   *    infusion.
   */
  public static boolean beenAdministered(
      Collection<MedicationAdministration> allAdminsPerEncounter,
      PeriodDt timeFrame, String medsSet) {
    allAdminsPerEncounter = freshestOfAllOrders(allAdminsPerEncounter).values();
    return allAdminsPerEncounter.stream()
        .filter(medicationAdministration -> medicationAdministration.getStatusElement().
            getValueAsEnum() == MedicationAdministrationStatusEnum.IN_PROGRESS
            || medicationAdministration.getStatusElement().getValueAsEnum()
            == MedicationAdministrationStatusEnum.COMPLETED)
        .filter(admin -> hasMedsSet(admin, medsSet))
        .filter(medicationAdministration -> insideTimeFrame(medicationAdministration, timeFrame))
        .anyMatch(medicationAdministration -> dosageOverZero(medicationAdministration));
  }

  /**
   * Returns true if there exists a MedicationAdministration for a specific encounter for
   * a given Meds Set name with
   *   - Status: In Progress or Completed
   *   - Inside a given time frame.
   *
   * @param medicationAdministrations
   *    A full list of med admins for a certain encounter.
   * @param timeFrame
   *    Time window constraint for search.
   * @param medsSet
   *    Meds group name that UCSF uses.
   * @return
   *    True if there is a medicationAdministration that matches criteria for in progress
   *    or completed administration.
   */
  public static boolean inProgressOrCompletedInTimeFrame(
      Collection<MedicationAdministration> medicationAdministrations,
      PeriodDt timeFrame, String medsSet) {
    medicationAdministrations = freshestOfAllOrders(medicationAdministrations).values();
    return medicationAdministrations.stream()
        .filter(medicationAdministration -> (medicationAdministration.getStatusElement()
            .getValueAsEnum() == MedicationAdministrationStatusEnum.IN_PROGRESS
            || medicationAdministration.getStatusElement().getValueAsEnum()
            == MedicationAdministrationStatusEnum.COMPLETED))
        .filter(admin -> hasMedsSet(admin, medsSet))
        .anyMatch(medicationAdministration -> insideTimeFrame(medicationAdministration, timeFrame));
  }

  /**
   * Returns true if there exists a MedicationAdministration for a given encounter for
   * a given Meds Set name with
   *   - Status: In Progress
   *   - Inside a given time frame.
   *
   * @param medicationAdministrations
   *    A full list of med admins for a certain encounter.
   * @param timeFrame
   *    Time window constraint for search.
   * @param medsSet
   *    Meds group name that UCSF uses.
   * @return
   *    True if there is a medicationAdministration that matches criteria for in progress
   *    administration.
   */
  public static boolean inProgressInTimeFrame(
      Collection<MedicationAdministration> medicationAdministrations,
      PeriodDt timeFrame, String medsSet) {
    medicationAdministrations = freshestOfAllOrders(medicationAdministrations).values();
    return medicationAdministrations.stream()
        .filter(medicationAdministration -> (medicationAdministration.getStatusElement().
            getValueAsEnum() == MedicationAdministrationStatusEnum.IN_PROGRESS))
        .filter(admin -> hasMedsSet(admin, medsSet))
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
   * @param administration
   *     Medication administration resource.
   * @return
   *     True if the supplied administration has a nonzero dosage.
   */
  public static boolean dosageOverZero(MedicationAdministration administration) {
    if (administration.getDosage() == null ||
        administration.getDosage().getQuantity() == null ||
        administration.getDosage().getQuantity().getValue() == null) {
      log.error("Medication administration id [{}] has a null dosage,"
          + " failing to process if the dosage is over zero.",
          administration.getIdentifierFirstRep().getValue());
      return false;
    }
    return administration.getDosage().getQuantity().getValue().compareTo(BigDecimal.ZERO) > 0;
  }

  /**
   * Returns the effective date of a medication administration.
   *
   * @param administration
   *     The administration to pull from.
   * @return The effective date.
   */
  public static Date getEffectiveDate(MedicationAdministration administration) {
    return ((DateTimeDt) administration.getEffectiveTime()).getValue();
  }

  /**
   * Return true if any identifier in a given admin matches the given meds set id.
   *
   * @param admin
   *    Order to search for a meds set id.
   * @param medsSet
   *    The meds set ID we are looking for.
   * @return
   *    All matching identifiers.
   */
  public static boolean hasMedsSet(MedicationAdministration admin, String medsSet) {
    if (MedicationAdministrationUtils.findIdentifiers(admin,
        CodingSystems.UCSF_MEDICATION_GROUP_NAME).stream()
        .anyMatch((ident) -> (medsSet.equals(ident.getValue())))) {
      return true;
    }
    return false;
  }

  /**
   * Finds an identifier for a given coding system. Given multiple matches, return all.
   *
   * @param admin
   *    Order to search for Identifiers.
   * @param codingSystem
   *    Coding system of the Identifier we want.
   * @return
   *    All matching identifiers.
   */
  public static List<IdentifierDt> findIdentifiers(MedicationAdministration admin,
      String codingSystem) {
    return admin.getIdentifier().stream().filter(ident -> ident.getSystem().equals(codingSystem))
        .collect(Collectors.toList());
  }
}
