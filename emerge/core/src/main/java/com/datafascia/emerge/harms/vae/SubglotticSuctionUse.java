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
