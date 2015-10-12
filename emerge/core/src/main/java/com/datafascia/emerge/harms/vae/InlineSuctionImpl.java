// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * In-line Suction Implementation
 */
@Slf4j
public class InlineSuctionImpl {
  // Private constructor disallows creating instances of this class.
  private InlineSuctionImpl() {
  }

  @Inject
  private ClientBuilder apiClient;

  private static final boolean DEFAULT_RESULT = false;

  /**
   * In-line Suction Implementation
   * Returns whether the encounter contains an Observation that indicates in-line suction
   *
   * @param encounterId
   *    The encounter to check.
   * @return
   *    True if there is in-line suction for this encounter.
   */
  public boolean getInlineSuction(String encounterId) {
    Calendar cal = Calendar.getInstance();

    cal.add(Calendar.HOUR, -13);
    Date thirteenHoursAgo = cal.getTime();

    Observation freshestInlinePlacement = ObservationUtils.findFreshestObservationForCode(apiClient,
        ObservationCodeEnum.INLINE_PLACEMENT.getCode(), encounterId);

    List<Observation> airwayDevices = ObservationUtils.getObservationByCodeAfterTime(apiClient,
        encounterId, ObservationCodeEnum.AIRWAY_DEVICE.getCode(), thirteenHoursAgo);

    if (freshestInlinePlacement != null) {
      switch (freshestInlinePlacement.getValue().toString()) {
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
          log.warn("Unexpected value for inline placement observation found: "
              + freshestInlinePlacement.getValue().toString());
      }
    }

    for (Observation device : airwayDevices) {
      if (device.getValue().toString().equals("Inline")) {
        return true;
      }
    }

    return DEFAULT_RESULT;
  }
}
