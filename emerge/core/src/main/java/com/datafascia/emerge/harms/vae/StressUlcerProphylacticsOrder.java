// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.valueset.MedicationOrderStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import javax.inject.Inject;

import static com.datafascia.emerge.ucsf.codes.MedicationOrderEnum.STRESS_ULCER_PROPHYLACTICS;

/**
 * VAE Stress Ulcer Prophylactics Order Status implementation
 */
public class StressUlcerProphylacticsOrder {

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if there is an active or draft order for stress ulcer prophylaxis.
   *
   * @param encounterId
   *     encounter to search
   * @return true if there is an active or draft order for stress ulcer prophylaxis.
   */
  public boolean haveStressUlcerProphylacticsOrder(String encounterId) {
    return apiClient.getMedicationOrderClient()
        .search(encounterId)
        .stream()
        .filter(order -> order.getIdentifier().stream()
            .anyMatch(ident -> ident.getValue()
                .equals(STRESS_ULCER_PROPHYLACTICS.getCode())))
        .anyMatch(order -> order.getStatusElement().getValueAsEnum()
            .equals(MedicationOrderStatusEnum.ACTIVE) || order.getStatusElement().getValueAsEnum()
            .equals(MedicationOrderStatusEnum.DRAFT));
  }
}
