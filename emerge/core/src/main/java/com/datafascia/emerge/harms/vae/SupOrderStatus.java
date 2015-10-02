// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import com.datafascia.api.client.ClientBuilder;

import static com.datafascia.emerge.ucsf.codes.MedicationOrderEnum.STRESS_ULCER_PROPHYLACTICS;

/**
 * VAE Harm Stress Ulcer Prophylactics Order Status implementation
 */
public class SupOrderStatus {
  /**
   * VAE Harm Stress Ulcer Prophylactics Order Status Implementation
   *
   * @param client
   *     the FHIR client used to query Topaz
   * @param encounterId
   *     the Encounter to search
   * @return
   *     True if there is an active or draft order for stress ulcer prophylaxis.
   */
  public static boolean supOrderStatus(ClientBuilder client, String encounterId) {
    return client.getMedicationOrderClient()
        .list(encounterId).stream()
        .filter(order -> order.getIdentifierFirstRep().getValue()
            .equals(STRESS_ULCER_PROPHYLACTICS.getCode()))
        .anyMatch(order -> order.getStatusElement().getValueAsEnum()
            .equals(MedicationOrderStatusEnum.ACTIVE) || order.getStatusElement().getValueAsEnum()
            .equals(MedicationOrderStatusEnum.DRAFT));
  }
}
