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
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import java.time.Instant;

/**
 * Encounter helper methods
 */
public class EncounterUtils {

  // Private constructor disallows creating instances of this class.
  private EncounterUtils() {
  }

  /**
   * Gets time when patient was first admitted or transferred into an ICU. If the patient was
   * never in an ICU, then returns the hospital admit time.
   *
   * @param encounter
   *     encounter to pull from
   * @return ICU admit time
   */
  public static Instant getIcuPeriodStart(Encounter encounter) {
    PeriodDt period = (encounter.getLocation().size() > 1)
        ? encounter.getLocation().get(1).getPeriod()
        : encounter.getPeriod();
    return period.getStart().toInstant();
  }
}
