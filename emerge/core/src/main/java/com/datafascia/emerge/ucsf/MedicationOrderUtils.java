// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import java.util.List;

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
}
