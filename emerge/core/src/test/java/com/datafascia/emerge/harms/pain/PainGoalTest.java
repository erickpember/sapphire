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

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.testUtils.TestResources;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link PainGoalImpl} test
 */
public class PainGoalTest extends PainGoalImpl {
  /**
   * Test of getPainGoal method, of class PainGoalImpl.
   */
  @Test
  public void testGetPainGoal_3args() {
    Instant testingTime = Instant.parse("2007-12-03T10:15:30.00Z");
    Instant midnight = Instant.parse("2007-12-03T00:00:00.00Z");

    PeriodDt lastThirteenHours = new PeriodDt();
    lastThirteenHours.setStart(Date.from(testingTime.minus(13, ChronoUnit.HOURS)),
        TemporalPrecisionEnum.MILLI);
    lastThirteenHours.setEnd(Date.from(testingTime),
        TemporalPrecisionEnum.MILLI);

    PeriodDt sinceMidnight = new PeriodDt();
    sinceMidnight.setStart(Date.from(midnight),
        TemporalPrecisionEnum.MILLI);
    sinceMidnight.setEnd(Date.from(testingTime),
        TemporalPrecisionEnum.MILLI);

    List<Observation> observations = new ArrayList<>();

    assertEquals(getPainGoal(new Observations(observations), lastThirteenHours, sinceMidnight), 11);

    Observation verbal3 = TestResources.createObservation(
        ObservationCodeEnum.VERBAL_PAIN_03.getCode(),
        "Severe", testingTime.minus(2, ChronoUnit.HOURS));
    Observation goal3 = TestResources.createObservation(
        ObservationCodeEnum.PAIN_GOAL_03.getCode(),
        "8", testingTime.minus(9, ChronoUnit.HOURS));
    Observation numerical2 = TestResources.createObservation(
        ObservationCodeEnum.NUMERICAL_PAIN_02.getCode(),
        "7", testingTime.minus(4, ChronoUnit.HOURS));

    observations = Arrays.asList(verbal3, goal3, numerical2);

    assertEquals(getPainGoal(new Observations(observations), lastThirteenHours, sinceMidnight), 8);
  }
}
