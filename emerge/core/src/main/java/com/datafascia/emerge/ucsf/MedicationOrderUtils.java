// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.IdentifierDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import java.time.Instant;
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
   * Returns true if a specified medicationOrder is before a specified time.
   *
   * @param medicationOrder
   *     MedicationOrder resource.
   * @param expirationTime
   *     Expiration time to compare.
   * @return
   *     True if the medication order's date ended is before the specified expiration time.
   */
  public static boolean isExpired(MedicationOrder medicationOrder, Date expirationTime) {
    IDatatype effectiveTime = medicationOrder.getDateEndedElement();
    if (effectiveTime != null && ((DateTimeDt) effectiveTime).getValue() != null
        && !((DateTimeDt) effectiveTime).getValue().equals(Date.from(Instant.EPOCH))) {
      return ((DateTimeDt) effectiveTime).getValue().before(expirationTime);
    }
    return false;
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
