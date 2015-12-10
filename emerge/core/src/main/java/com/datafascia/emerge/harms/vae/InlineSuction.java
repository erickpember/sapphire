// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Checks if there is inline suction
 */
@Slf4j
public class InlineSuction {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if there is inline suction.
   *
   * @param encounterId
   *     encounter to search
   * @return true if there is inline suction for this encounter
   */
  public boolean test(String encounterId) {
    Instant now = Instant.now(clock);
    Date effectiveLowerBound = Date.from(now.minus(13, ChronoUnit.HOURS));

    Observation freshestInlinePlacement = ObservationUtils.findFreshestForCode(
        apiClient,
        encounterId,
        ObservationCodeEnum.INLINE_PLACEMENT.getCode());

    List<Observation> airwayDevices = ObservationUtils.getByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.AIRWAY_DEVICE.getCode(),
        effectiveLowerBound);

    if (freshestInlinePlacement != null) {
      String value = freshestInlinePlacement.getValue().toString();
      switch (value) {
        case "Placed":
        case "Changed":
          return true;
        case "Removed":
          for (Observation device : airwayDevices) {
            if (device.getValue().toString().equals("Inline")) {
              if (ObservationUtils.firstIsFresher(device, freshestInlinePlacement)) {
                return true;
              }
            }
          }
          return false;
        default:
          log.warn("Unexpected value for inline placement observation [{}]", value);
      }
    }

    for (Observation device : airwayDevices) {
      if (device.getValue().toString().equals("Inline")) {
        return true;
      }
    }

    return false;
  }
}
