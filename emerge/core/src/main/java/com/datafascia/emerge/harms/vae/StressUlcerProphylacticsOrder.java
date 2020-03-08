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
