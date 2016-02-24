// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.MedicationOrderUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import javax.inject.Inject;


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
                .equals(MedsSetEnum.STRESS_ULCER_PROPHYLACTICS.getCode())))
        .anyMatch(order -> MedicationOrderUtils.isActiveOrDraft(order));
  }
}
