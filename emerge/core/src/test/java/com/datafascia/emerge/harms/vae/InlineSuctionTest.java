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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * {@link InlineSuction} test
 */
public class InlineSuctionTest extends InlineSuction {

  /**
   * Test of test method, of class InlineSuction.
   */
  @Test
  public void testTest_Observations_Instant() {
    Observation newPlacement = createObservation(ObservationCodeEnum.INLINE_PLACEMENT.getCode(),
        "Placed", Instant.now().minus(1, ChronoUnit.MINUTES));
    Observation oldPlacement = createObservation(ObservationCodeEnum.INLINE_PLACEMENT.getCode(),
        "Changed", Instant.now().minus(14, ChronoUnit.HOURS));
    Observation newRemoved = createObservation(ObservationCodeEnum.INLINE_PLACEMENT.getCode(),
        "Removed", Instant.now().minus(1, ChronoUnit.MINUTES));

    Observation newDevice = createObservation(ObservationCodeEnum.AIRWAY_DEVICE.getCode(), "Inline",
        Instant.now().minus(1, ChronoUnit.SECONDS));
    Observation oldDevice = createObservation(ObservationCodeEnum.AIRWAY_DEVICE.getCode(), "Inline",
        Instant.now().minus(14, ChronoUnit.HOURS));

    assertTrue(test(new Observations(Arrays.asList(newPlacement, oldDevice)), Instant.now()));
    assertTrue(test(new Observations(Arrays.asList(oldPlacement, oldDevice)), Instant.now()));
    assertTrue(test(new Observations(Arrays.asList(newPlacement, newDevice)), Instant.now()));
    assertFalse(test(new Observations(Arrays.asList(newRemoved, oldDevice)), Instant.now()));
  }

  private Observation createObservation(String code, String value, Instant time) {
    DateTimeDt effectiveTime = new DateTimeDt(Date.from(time));
    Observation observation = new Observation()
        .setCode(new CodeableConceptDt("system", code))
        .setValue(new StringDt(value))
        .setIssued(new Date(), TemporalPrecisionEnum.SECOND)
        .setEffective(effectiveTime);
    observation.setId(this.getClass().getSimpleName() + code);
    return observation;
  }
}
