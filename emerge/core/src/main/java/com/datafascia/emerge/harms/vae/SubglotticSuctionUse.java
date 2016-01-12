// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.MaybeEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import javax.inject.Inject;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * Checks for sub-glottic suction use
 */
@Slf4j
public class SubglotticSuctionUse {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks for sub-glottic suction use.
   *
   * @param encounterId
   *     encounter to search
   * @return
   *     "Yes", "No" or "Not Documented" depending on conditions
   */
  public MaybeEnum apply(String encounterId) {
    Instant now = Instant.now(clock);
    Instant effectiveLowerBound = now.minus(7, ChronoUnit.HOURS);
    Observations observations = apiClient.getObservationClient().list(encounterId);

    Optional<Observation> freshestSubglotticStatus = observations.findFreshest(
        ObservationCodeEnum.SUBGLOTTIC_SUCTION.getCode(), effectiveLowerBound, now);
    if (!freshestSubglotticStatus.isPresent()) {
      return MaybeEnum.NOT_DOCUMENTED;
    }

    String value = ObservationUtils.getValueAsString(freshestSubglotticStatus.get());

    if (Strings.isNullOrEmpty(value)) {
      return MaybeEnum.NOT_DOCUMENTED;
    }

    switch (value) {
      case "Connected/In use":
        return MaybeEnum.YES;
      case "Other (Comment)":
      case "Capped off":
        return MaybeEnum.NO;
      default:
        log.warn("Unrecognized subglottic status [{}]", value);
        return MaybeEnum.NOT_DOCUMENTED;
    }
  }
}
