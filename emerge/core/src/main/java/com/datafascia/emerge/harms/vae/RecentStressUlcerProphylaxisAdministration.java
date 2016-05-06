// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
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
    PeriodDt supPeriod = new PeriodDt();
    supPeriod.setStart(twentyFiveHoursAgo, TemporalPrecisionEnum.SECOND);
    supPeriod.setEnd(Date.from(now), TemporalPrecisionEnum.SECOND);

    return MedicationAdministrationUtils.beenAdministered(
        admins, supPeriod, MedsSetEnum.STRESS_ULCER_PROPHYLACTICS.getCode());
  }
}
