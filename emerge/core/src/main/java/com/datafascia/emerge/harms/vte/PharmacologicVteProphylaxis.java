// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.resource.MedicationOrder;
import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import java.util.List;
import javax.inject.Inject;

/**
 * Pharmacologic VTE Prophylaxis implementation
 */
public class PharmacologicVteProphylaxis {

  @Inject
  private ClientBuilder apiClient;

  /**
   * Gets Pharmacologic VTE Prophylaxis Type
   *
   * @param encounterId
   *     encounter to search
   * @return pharmacologic VTE prophylaxis type ordered, or {@code null} if not found
   */
  public String getPharmacologicVteProphylaxisType(String encounterId) {
    String type = null;

    List<MedicationOrder> medicationOrders = apiClient.getMedicationOrderClient()
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
   * Checks if Pharmacologic VTE Prophylaxis was ordered
   *
   * @param encounterId
   *     encounter to search
   * @return true if pharmacologic VTE prophylaxis was ordered
   */
  public boolean isPharmacologicVteProphylaxisOrdered(String encounterId) {
    return getPharmacologicVteProphylaxisType(encounterId) != null;
  }
}
