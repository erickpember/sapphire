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
import java.util.Arrays;
import java.util.Date;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link VerbalPainLevel} test
 */
public class VerbalPainLevelTest extends VerbalPainLevel {
  /**
   * Test of getAllVerbalPainLevels method, of class VerbalPainLevel.
   */
  @Test
  public void testGetAllVerbalPainLevels_3args() {
    Instant testingTime = Instant.parse("2007-12-03T10:15:30.00Z");
    Instant midnight = Instant.parse("2007-12-03T00:00:00.00Z");

    Observation maxPain = TestResources.createObservation(
        ObservationCodeEnum.VERBAL_PAIN_01.getCode(),
        "Severe", testingTime.minus(2, ChronoUnit.HOURS));

    Observation oldMaxPain = TestResources.createObservation(
        ObservationCodeEnum.VERBAL_PAIN_01.getCode(),
        "Moderate", testingTime.minus(11, ChronoUnit.HOURS));

    Observation oldMinPain = TestResources.createObservation(
        ObservationCodeEnum.VERBAL_PAIN_01.getCode(),
        "None", testingTime.minus(13, ChronoUnit.HOURS));

    Observation minPain = TestResources.createObservation(
        ObservationCodeEnum.VERBAL_PAIN_02.getCode(),
        "Mild", testingTime.minus(3, ChronoUnit.HOURS));

    Observation currentPain = TestResources.createObservation(
        ObservationCodeEnum.VERBAL_PAIN_03.getCode(),
        "Moderate", testingTime.minus(1, ChronoUnit.HOURS));

    Observations observations = new Observations(
        Arrays.asList(maxPain, oldMaxPain, oldMinPain, minPain, currentPain));

    PeriodDt currentPainTimeRange = new PeriodDt();
    currentPainTimeRange.setStart(Date.from(testingTime.minus(7, ChronoUnit.HOURS)),
        TemporalPrecisionEnum.MILLI);
    currentPainTimeRange.setEnd(Date.from(testingTime),
        TemporalPrecisionEnum.MILLI);

    PeriodDt painMinMaxTimeRange = new PeriodDt();
    painMinMaxTimeRange.setStart(Date.from(midnight),
        TemporalPrecisionEnum.MILLI);
    painMinMaxTimeRange.setEnd(Date.from(testingTime),
        TemporalPrecisionEnum.MILLI);

    AllVerbalPainLevels result = getAllVerbalPainLevels(observations,
        currentPainTimeRange, painMinMaxTimeRange);

    assertEquals(result.getCurrent().getPainScore(), 5);
    assertEquals(result.getMax().getPainScore(), 7);
    assertEquals(result.getMin().getPainScore(), 1);
  }
}
