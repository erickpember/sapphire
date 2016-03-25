// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.cpot;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.harms.pain.CpotImpl;
import com.datafascia.emerge.testUtils.TestResources;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link CpotImpl} test
 */
public class CpotTest extends CpotImpl {
  /**
   * Test of getAllCpotLevels method, of class CpotImpl.
   */
  @Test
  public void testGetAllCpotLevels_3args() {
    Instant testingTime = Instant.parse("2007-12-03T10:15:30.00Z");
    Instant midnight = Instant.parse("2007-12-03T00:00:00.00Z");

    Observation maxCpot = TestResources.createObservation(
        ObservationCodeEnum.CPOT.getCode(),
        "7", testingTime.minus(2, ChronoUnit.HOURS));
    Observation oldMaxCpot = TestResources.createObservation(
        ObservationCodeEnum.CPOT.getCode(),
        "8", testingTime.minus(11, ChronoUnit.HOURS));
    Observation oldMinCpot = TestResources.createObservation(
        ObservationCodeEnum.CPOT.getCode(),
        "1", testingTime.minus(13, ChronoUnit.HOURS));
    Observation minCpot = TestResources.createObservation(
        ObservationCodeEnum.CPOT.getCode(),
        "2", testingTime.minus(3, ChronoUnit.HOURS));
    Observation currentCpot = TestResources.createObservation(
        ObservationCodeEnum.CPOT.getCode(),
        "3", testingTime.minus(1, ChronoUnit.HOURS));

    Observations observations = new Observations(
        Arrays.asList(maxCpot, oldMaxCpot, oldMinCpot, minCpot, currentCpot));

    PeriodDt currentCpotTimeRange = new PeriodDt();
    currentCpotTimeRange.setStart(Date.from(testingTime.minus(7, ChronoUnit.HOURS)),
        TemporalPrecisionEnum.MILLI);
    currentCpotTimeRange.setEnd(Date.from(testingTime),
        TemporalPrecisionEnum.MILLI);

    PeriodDt cpotMinMaxTimeRange = new PeriodDt();
    cpotMinMaxTimeRange.setStart(Date.from(midnight),
        TemporalPrecisionEnum.MILLI);
    cpotMinMaxTimeRange.setEnd(Date.from(testingTime),
        TemporalPrecisionEnum.MILLI);

    AllCpotLevels result = getAllCpotLevels(observations,
        currentCpotTimeRange, cpotMinMaxTimeRange);

    assertEquals(result.getCurrent().getPainScore(), 3);
    assertEquals(result.getMax().getPainScore(), 7);
    assertEquals(result.getMin().getPainScore(), 2);
  }
}
