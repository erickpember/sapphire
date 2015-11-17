// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.MaybeEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Checks for subglottic suction use
 */
@Slf4j
public class SubglotticSuctionUse {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks for subglottic suction use.
   *
   * @param encounterId
   *     encounter to search
   * @return
   *     "Yes", "No" or "Not Documented" depending on conditions
   */
  public MaybeEnum apply(String encounterId) {
    Instant now = Instant.now(clock);
    Date effectiveLowerBound = Date.from(now.minus(7, ChronoUnit.HOURS));

    List<Observation> observations = ObservationUtils.getObservationByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.SUBGLOTTIC_SUCTION.getCode(),
        effectiveLowerBound);
    if (observations.isEmpty()) {
      return MaybeEnum.NOT_DOCUMENTED;
    }

    Observation freshestSubglotticStatus = ObservationUtils.findFreshestObservation(observations);

    if (freshestSubglotticStatus != null && freshestSubglotticStatus.getValue() != null) {
      String value = freshestSubglotticStatus.getValue().toString();
      switch (value) {
        case "Connected/In use":
          return MaybeEnum.YES;
        case "Capped off":
          return MaybeEnum.NO;
        case "Other (Comment)":
          return MaybeEnum.NOT_DOCUMENTED;
        default:
          log.warn("Unrecognized subglottic status [{}]", value);
      }
    }

    return MaybeEnum.NOT_DOCUMENTED;
  }
}
