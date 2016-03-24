// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
 * {@link NumericalPainLevel} test
 */
public class NumericPainLevelTest extends NumericalPainLevel {
  /**
   * Test of getAllNumericPainLevels method, of class NumericalPainLevel.
   */
  @Test
  public void testGetAllNumericPainLevels_3args() {
    Instant testingTime = Instant.parse("2007-12-03T10:15:30.00Z");
    Instant midnight = Instant.parse("2007-12-03T00:00:00.00Z");

    Observation maxPain = TestResources.createObservation(
        ObservationCodeEnum.NUMERICAL_PAIN_01.getCode(),
        "7", testingTime.minus(2, ChronoUnit.HOURS));
    Observation oldMaxPain = TestResources.createObservation(
        ObservationCodeEnum.NUMERICAL_PAIN_01.getCode(),
        "8", testingTime.minus(11, ChronoUnit.HOURS));
    Observation oldMinPain = TestResources.createObservation(
        ObservationCodeEnum.NUMERICAL_PAIN_01.getCode(),
        "1", testingTime.minus(13, ChronoUnit.HOURS));
    Observation minPain = TestResources.createObservation(
        ObservationCodeEnum.NUMERICAL_PAIN_02.getCode(),
        "2", testingTime.minus(3, ChronoUnit.HOURS));
    Observation currentPain = TestResources.createObservation(
        ObservationCodeEnum.NUMERICAL_PAIN_03.getCode(),
        "3", testingTime.minus(1, ChronoUnit.HOURS));

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

    AllNumericPainLevels result = getAllNumericPainLevels(observations,
        currentPainTimeRange, painMinMaxTimeRange);

    assertEquals(result.getCurrent().getPainScore(), 3);
    assertEquals(result.getMax().getPainScore(), 7);
    assertEquals(result.getMin().getPainScore(), 2);
  }
}
