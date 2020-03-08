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

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Checks if there is in-line suction
 */
@Slf4j
public class InlineSuction {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if there is in-line suction.
   *
   * @param encounterId
   *     encounter to search
   * @return true if there is in-line suction for this encounter
   */
  public boolean test(String encounterId) {
    Instant now = Instant.now(clock);

    Observations observations = apiClient.getObservationClient().list(encounterId);

    return test(observations, now);
  }

  /**
   * Checks if there is in-line suction. Encapsulates non-API operations for testing.
   *
   * @param observations
   *     A container of observations for this encounter.
   * @param now
   *     The current time.
   * @return true if there is in-line suction for this encounter
   */
  public boolean test(Observations observations, Instant now) {
    Instant effectiveLowerBound = now.minus(13, ChronoUnit.HOURS);

    Optional<Observation> freshestInlinePlacement = observations.findFreshest(
        ObservationCodeEnum.INLINE_PLACEMENT.getCode(), null, null);
    List<Observation> airwayDevices = observations.list(
        ObservationCodeEnum.AIRWAY_DEVICE.getCode(), effectiveLowerBound, null);

    if (freshestInlinePlacement.isPresent()) {
      switch (ObservationUtils.getValueAsString(freshestInlinePlacement.get())) {
        case "Placed":
        case "Changed":
          return true;
        case "Removed":
          for (Observation device : airwayDevices) {
            if (device.getValue().toString().equals("Inline")) {
              if (ObservationUtils.firstIsFresher(device, freshestInlinePlacement.get())) {
                return true;
              }
            }
          }
          return false;
        default:
          log.warn("Unexpected value for inline placement observation [{}]", ObservationUtils
              .getValueAsString(freshestInlinePlacement.get()));
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
