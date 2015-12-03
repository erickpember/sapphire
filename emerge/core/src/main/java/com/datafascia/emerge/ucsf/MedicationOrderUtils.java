// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * MedicationOrder helper methods
 */
public class MedicationOrderUtils {

  // Private constructor disallows creating instances of this class.
  private MedicationOrderUtils() {
  }

  /**
   * Finds freshest MedicationOrder.
   *
   * @param medicationOrders
   *     MedicationOrders to search
   * @return freshest medicationOrder, or {@code null} if input medicationOrders is empty
   */
  public static MedicationOrder findFreshestMedicationOrder(
      List<MedicationOrder> medicationOrders) {
    return medicationOrders.stream()
        .max(new MedicationOrderDateWrittenComparator())
        .orElse(null);
  }

  /**
   * Finds freshest MedicationOrder for status of either Active or Draft.
   *
   * @param encounterId
   *    encounter to search for medication orders
   * @param client
   *    API client
   * @return
   *    freshest medicationOrder for status of either Active or Draft,
   *    or {@code null} if no match is found.
   */
  public static MedicationOrder findFreshestActiveOrDraftMedicationOrder(ClientBuilder client,
      String encounterId) {
    List<MedicationOrder> medicationOrders = client.getMedicationOrderClient().search(encounterId,
        MedicationOrderStatusEnum.ACTIVE.getCode());
    medicationOrders.addAll(client.getMedicationOrderClient().search(encounterId,
        MedicationOrderStatusEnum.DRAFT.getCode()));
    return medicationOrders.stream()
        .max(new MedicationOrderDateWrittenComparator())
        .orElse(null);
  }

  /**
   * Finds any MedicationOrder for a given medication set name with a date written before
   * a given time.
   *
   * @param encounterId
   *     Encounter to search for medication orders.
   * @param client
   *     API client.
   * @param medsSet
   *     Medication identifier, AKA MedsSet, optional.
   * @param endTime
   *     End time for search.
   * @return
   *     Medication Orders for a given meds set before a given time or {@code null}
   *     if no match is found.
   */
  public static List<MedicationOrder> findMedicationBefore(
      ClientBuilder client, String encounterId, String medsSet, Date endTime) {
    List<MedicationOrder> medicationOrders = client.getMedicationOrderClient().search(encounterId,
        null, medsSet);
    return medicationOrders.stream()
        .filter(order -> isBefore(order, endTime))
        .collect(Collectors.toList());
  }

  /**
   * Finds an identifier for a given coding system. Given multiple matches, return all.
   *
   * @param order
   *    Order to search for Identifiers.
   * @param codingSystem
   *    Coding system of the Identifier we want.
   * @return
   *    All matching identifiers.
   */
  public static List<IdentifierDt> findIdentifiers(MedicationOrder order, String codingSystem) {
    return order.getIdentifier().stream().filter(ident -> ident.getSystem().equals(codingSystem))
        .collect(Collectors.toList());
  }

  /**
   * Returns true if a specified medicationOrder  is before a specified time.
   *
   * @param medicationOrder
   *     MedicationOrder resource.
   * @param startTime
   *     Start time for search.
   * @return
   *     True if the medication order's date written time is before the specified start time.
   */
  public static boolean isBefore(MedicationOrder medicationOrder, Date startTime) {
    IDatatype effectiveTime = medicationOrder.getDateWrittenElement();
    return ((DateTimeDt) effectiveTime).getValue().before(startTime);
  }

  /**
   * Returns true if a specified medication order has a status of active or draft.
   *
   * @param order
   *     medication order
   * @return true if the medication order has a status of active or draft
   */
  public static boolean isActiveOrDraft(MedicationOrder order) {
    if (order == null) {
      return false;
    }

    MedicationOrderStatusEnum status = order.getStatusElement().getValueAsEnum();
    return status == MedicationOrderStatusEnum.ACTIVE || status == MedicationOrderStatusEnum.DRAFT;
  }
}
