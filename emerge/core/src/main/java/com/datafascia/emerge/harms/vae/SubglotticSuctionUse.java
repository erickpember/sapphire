// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.MaybeEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * VAE Harm Subglottic Suction Use Implementation
 */
@Slf4j
public class SubglotticSuctionUse {
  // Private constructor disallows creating instances of this class.
  private SubglotticSuctionUse() {
  }

  @Inject
  private ClientBuilder apiClient;

  private static final String DEFAULT_RESULT = MaybeEnum.NOT_DOCUMENTED.getCode();

  /**
   * VAE Harm Subglottic Suction Use Implementation
   * Returns whether the encounter contains an Observation that indicates Subglottic Suction Use.
   *
   * @param encounterId
   *    The encounter to check.
   * @return
   *    "Yes", "No" or "Not Documented" depending on if conditions are met.
   */
  public String getSubGlotticSuctionUse(String encounterId) {
    Calendar cal = Calendar.getInstance();

    cal.add(Calendar.HOUR, -7);
    Date sevenHoursAgo = cal.getTime();

    List<Observation> observations = ObservationUtils.getObservationByCodeAfterTime(apiClient,
        encounterId, ObservationCodeEnum.SUBGLOTTIC_SUCTION.getCode(), sevenHoursAgo);

    if (observations == null || observations.isEmpty()) {
      return MaybeEnum.NOT_DOCUMENTED.getCode();
    }

    Observation freshestSubglotticStatus = ObservationUtils.findFreshestObservation(observations);

    switch (freshestSubglotticStatus.getValue().toString()) {
      case "Connected/In use":
        return MaybeEnum.YES.getCode();
      case "Capped off":
        return MaybeEnum.NO.getCode();
      case "Other (Comment)":
        return MaybeEnum.NOT_DOCUMENTED.getCode();
      default:
        log.warn("Unrecognized subglottic status found: " + freshestSubglotticStatus.getValue()
            .toString());
    }

    return DEFAULT_RESULT;
  }
}
