// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import java.util.List;

/**
 * Pharmacologic VTE Prophylaxis Ordered implementation
 */
public class PharmacologicVtePpx {
  /**
   * Pharmacologic VTE Prophylaxis Type Implementation
   *
   * @param client
   *     the FHIR client used to search Topaz
   * @param encounterId
   *     the Encounter to search
   * @return type
   *     the specific pharmacologic VTE prophylaxis ordered
   */
  public static String pharmacologicVtePpxType(ClientBuilder client, String encounterId) {
    String type = null;

    List<MedicationOrder> medicationOrders = client.getMedicationOrderClient()
        .search(encounterId);
    for (MedicationOrder medicationOrder : medicationOrders) {
      if (medicationOrder.getStatusElement().getValueAsEnum() == MedicationOrderStatusEnum.ACTIVE) {
        String medicationOrderIdentifier = medicationOrder.getIdentifierFirstRep().getValue();
        if (medicationOrderIdentifier.equals("Intermittent Enoxaparin SC") ||
            medicationOrderIdentifier.equals("Intermittent Heparin SC")) {
          type = medicationOrder.getIdentifierFirstRep().getValue();
        }
      }
    }
    return type;
  }

  /**
   * Pharmacologic VTE Prophylaxis Type Implementation
   *
   * @param client
   *     the FHIR client used to query Topaz
   * @param encounterId
   *     the Encounter to search
   * @return type
   *     the specific pharmacologic VTE prophylaxis ordered
   */
  public static boolean pharmacologicVtePpxOrdered(ClientBuilder client, String encounterId) {
    return pharmacologicVtePpxType(client, encounterId) != null;
  }
}
