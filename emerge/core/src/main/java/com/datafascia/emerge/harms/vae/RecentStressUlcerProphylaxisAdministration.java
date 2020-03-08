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

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Checks if there is a recent stress ulcer prophylaxis administration.
 */
@Slf4j
public class RecentStressUlcerProphylaxisAdministration {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  private static final long SUP_LOOKBACK = 25;

  /**
   * Checks if there is a recent stress ulcer prophylaxis administration.
   *
   * @param encounterId
   *     encounter to search
   * @return true if there is a recent stress ulcer prophylaxis administration
   */
  public boolean test(String encounterId) {
    List<MedicationAdministration> admins = apiClient.getMedicationAdministrationClient()
        .search(encounterId);
    return test(admins, encounterId, Instant.now(clock), apiClient);
  }

  /**
   * Checks if there is a recent stress ulcer prophylaxis administration.
   *
   * @param admins
   *     Medication administrations for this encounter.
   * @param encounterId
   *     encounter to search
   * @param now
   *     The current time.
   * @param client
   *     The API client.
   * @return true if there is a recent stress ulcer prophylaxis administration
   */
  public boolean test(List<MedicationAdministration> admins, String encounterId, Instant now,
      ClientBuilder client) {
    Date twentyFiveHoursAgo = Date.from(now.minus(SUP_LOOKBACK, ChronoUnit.HOURS));

    return admins.stream()
        .filter(admin -> MedicationAdministrationUtils.hasMedsSet(admin,
            MedsSetEnum.STRESS_ULCER_PROPHYLACTICS.getCode()))
        .filter(admin -> MedicationAdministrationUtils.dosageOverZero(admin))
        .anyMatch(admin -> (admin.getReasonNotGiven().isEmpty() &&
            (admin.getStatusElement().getValueAsEnum() ==
                MedicationAdministrationStatusEnum.IN_PROGRESS ||
             admin.getStatusElement().getValueAsEnum() ==
                MedicationAdministrationStatusEnum.COMPLETED) &&
            (MedicationAdministrationUtils.isAfter(admin, twentyFiveHoursAgo))));
  }
}
