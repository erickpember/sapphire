// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.rass;

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
 * {@link RassLevel} test
 */
public class RassLevelTest extends RassLevel {
  /**
   * Test of getAllRassLevels method, of class RassLevel.
   */
  @Test
  public void testGetAllRassLevels_3args() {
    Instant testingTime = Instant.parse("2007-12-03T10:15:30.00Z");
    Instant midnight = Instant.parse("2007-12-03T00:00:00.00Z");

    Observation maxRass = TestResources.createObservation(
        ObservationCodeEnum.RASS.getCode(),
        "7", testingTime.minus(2, ChronoUnit.HOURS));
    Observation oldMaxRass = TestResources.createObservation(
        ObservationCodeEnum.RASS.getCode(),
        "8", testingTime.minus(11, ChronoUnit.HOURS));
    Observation oldMinRass = TestResources.createObservation(
        ObservationCodeEnum.RASS.getCode(),
        "-5", testingTime.minus(13, ChronoUnit.HOURS));
    Observation minRass = TestResources.createObservation(
        ObservationCodeEnum.RASS.getCode(),
        "-4", testingTime.minus(3, ChronoUnit.HOURS));
    Observation currentRass = TestResources.createObservation(
        ObservationCodeEnum.RASS.getCode(),
        "2", testingTime.minus(1, ChronoUnit.HOURS));

    Observations observations = new Observations(
        Arrays.asList(maxRass, oldMaxRass, oldMinRass, minRass, currentRass));

    PeriodDt currentRassTimeRange = new PeriodDt();
    currentRassTimeRange.setStart(Date.from(testingTime.minus(7, ChronoUnit.HOURS)),
        TemporalPrecisionEnum.MILLI);
    currentRassTimeRange.setEnd(Date.from(testingTime),
        TemporalPrecisionEnum.MILLI);

    PeriodDt rassMinMaxTimeRange = new PeriodDt();
    rassMinMaxTimeRange.setStart(Date.from(midnight),
        TemporalPrecisionEnum.MILLI);
    rassMinMaxTimeRange.setEnd(Date.from(testingTime),
        TemporalPrecisionEnum.MILLI);

    AllRassLevels result = getAllRassLevels(observations,
        currentRassTimeRange, rassMinMaxTimeRange);

    assertEquals(result.getCurrent().getRassScore(), 2);
    assertEquals(result.getMax().getRassScore(), 7);
    assertEquals(result.getMin().getRassScore(), -4);
  }
}
