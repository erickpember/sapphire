// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.MedicationAdministrationUtils;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.List;
import javax.inject.Inject;

/**
 * Checks if there is a recent stress ulcer prophylaxis administration.
 */
public class RecentStressUlcerProphylaxisAdministration {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  private static final long ACTIVELY_INFUSING_LOOKBACK = 4;
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
    return MedicationAdministrationUtils.freshestOfAllOrders(admins).values()
        .stream()
        .filter(administration -> administration.getReasonNotGiven().isEmpty())
        .anyMatch(admin -> MedicationAdministrationUtils.isActivelyInfusing(
                admin,
                MedsSetEnum.STRESS_ULCER_PROPHYLACTICS.getCode(),
                now,
                ACTIVELY_INFUSING_LOOKBACK + SUP_LOOKBACK,
                client,
                encounterId));
  }
}
