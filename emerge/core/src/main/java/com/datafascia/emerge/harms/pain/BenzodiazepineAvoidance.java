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
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import javax.inject.Inject;

/**
 * Utilities related to benzodiazepine avoidance.
 */
public class BenzodiazepineAvoidance {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Determines whether benzodiazepine avoidance is contraindicated.
   *
   * @param encounterId
   *     The encounter to check.
   * @return Whether benzodiazepine avoidance is contraindicated.
   */
  public boolean isBenzodiazepineAvoidanceContraindicated(String encounterId) {
    Observations observations = apiClient.getObservationClient().list(encounterId);

    return isBenzodiazepineAvoidanceContraindicated(observations, Instant.now(clock));
  }

  /**
   * Determines whether benzodiazepine avoidance is contraindicated.
   *
   * @param observations
   *     The observations for an encounter.
   * @param now
   *     The current time.
   * @return Whether benzodiazepine avoidance is contraindicated.
   */
  public boolean isBenzodiazepineAvoidanceContraindicated(Observations observations, Instant now) {
    Instant twentyFourHoursAgo = now.minus(24, ChronoUnit.HOURS);

    List<Observation> filteredObservations = observations.list(
        ObservationCodeEnum.BENZODIAZEPINE_AVOIDANCE.getCode(),
        twentyFourHoursAgo, null);

    for (Observation observation : filteredObservations) {
      if (observation.getValue() instanceof QuantityDt
          && ((QuantityDt) observation.getValue()).getValue().doubleValue() > 0) {
        return true;
      }
    }

    return false;
  }
}
