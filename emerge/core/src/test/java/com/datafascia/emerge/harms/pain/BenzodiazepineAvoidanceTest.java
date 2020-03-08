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

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.testUtils.TestResources;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests Benzodiazepine Avoidance logic without the API.
 */
public class BenzodiazepineAvoidanceTest extends BenzodiazepineAvoidance {

  private static final String code = ObservationCodeEnum.BENZODIAZEPINE_AVOIDANCE.getCode();

  /**
   * Test of isBenzodiazepineAvoidanceContraindicated method, of class BenzodiazepineAvoidance.
   */
  @Test
  public void testIsBenzodiazepineAvoidanceContraindicated() {
    Instant twentyThreeHoursAgo = Instant.now().minus(23, ChronoUnit.HOURS);
    Instant twentyFiveHoursAgo = Instant.now().minus(25, ChronoUnit.HOURS);
    Instant now = Instant.now();

    Observation tooEarly = TestResources.createObservation(code, 9.0, twentyFiveHoursAgo);
    Observation tooSmall = TestResources.createObservation(code, 0.0, twentyThreeHoursAgo);
    Observation justRight = TestResources.createObservation(code, 9.0, twentyThreeHoursAgo);

    assertFalse(isBenzodiazepineAvoidanceContraindicated(new Observations(Arrays.asList(tooEarly,
        tooSmall)), now));
    assertTrue(isBenzodiazepineAvoidanceContraindicated(new Observations(Arrays.asList(tooEarly,
        tooSmall, justRight)), now));
  }
}
