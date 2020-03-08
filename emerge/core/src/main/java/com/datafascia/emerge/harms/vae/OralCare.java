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
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.MaybeEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Checks for oral care
 */
@Slf4j
public class OralCare {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  private static final Set<String> YES_VALUES = ImmutableSet.of(
      "Teeth brushed with CHG",
      "Suction toothette with H. Peroxide",
      "Mouth swabbed",
      "Teeth brushed");
  private static final Set<String> NO_VALUES = ImmutableSet.of(
      "Patient refused",
      "Other (Comment)");
  private static final Set<String> CONTRAINDICATED_VALUES = ImmutableSet.of(
      "Contraindicated (bleeding, \"no oral care\" order)",
      "Patient unavailable (off unit, procedure in progress)");

  /**
   * Checks for oral care.
   *
   * @param encounterId
   *     encounter to search
   * @return
   *     "Yes", "No", "Not Documented" or "Contraindicated" depending on condition
   */
  public MaybeEnum apply(String encounterId) {
    Instant now = Instant.now(clock);
    Date effectiveLowerBound = Date.from(now.minus(5, ChronoUnit.HOURS));

    Optional<Observation> freshestOralCareAction = ObservationUtils.getFreshestByCodeAfterTime(
        apiClient,
        encounterId,
        ObservationCodeEnum.ORAL_CARE.getCode(),
        effectiveLowerBound);

    if (freshestOralCareAction.isPresent()) {
      String value = freshestOralCareAction.get().getValue().toString();
      if (YES_VALUES.stream().anyMatch(yesValue -> value.contains(yesValue))) {
        return MaybeEnum.YES;
      } else if (NO_VALUES.stream().anyMatch(noValue -> value.contains(noValue))) {
        return MaybeEnum.NO;
      } else if (CONTRAINDICATED_VALUES.stream().anyMatch(contraindicatedValue -> value.contains(
          contraindicatedValue))) {
        return MaybeEnum.CONTRAINDICATED;
      } else {
        log.warn("Unrecognized value for oral care observation [{}]", value);
      }
    }

    return MaybeEnum.NO;
  }
}
