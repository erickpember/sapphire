// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
 * {@link Ventilated} test
 */
public class VentilatedTest extends Ventilated {

  public VentilatedTest() {
  }

  /**
   * Test of isVentilated method, of class Ventilated.
   */
  @Test
  public void testIsVentilated_Observations_Instant() {
    Instant icuAdmitTime = Instant.now().minus(23, ChronoUnit.HOURS);
    Instant now = Instant.now();

    Observation unrelatedObservation = createObservation(
        "999999999",
        "Continue", Instant.now().minus(1, ChronoUnit.HOURS));
    Observation peepObservation = createObservation(
        ObservationCodeEnum.PEEP.getCode(),
        "12345", Instant.now().minus(1, ChronoUnit.HOURS));
    Observation newEttInvasiveStatus = createObservation(
        ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(),
        "Continue", Instant.now().minus(1, ChronoUnit.HOURS));
    Observation newEttInvasiveStatus2 = createObservation(
        ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(),
        "Trial in progress", Instant.now().minus(2, ChronoUnit.HOURS));
    Observation newEttInvasiveStatus3 = createObservation(
        ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(),
        "Trial terminated", Instant.now().minus(2, ChronoUnit.HOURS));
    Observation oldEttInvasiveStatus = createObservation(
        ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(),
        "Patient taken off", Instant.now().minus(3, ChronoUnit.HOURS));

    Observation newTrachInvasiveStatus = createObservation(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode(),
        "Patient back on Invasive", Instant.now().minus(2, ChronoUnit.HOURS));
    Observation newTrachInvasiveStatus2 = createObservation(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode(),
        "Trial in progress", Instant.now().minus(2, ChronoUnit.HOURS));
    Observation newTrachInvasiveStatus3 = createObservation(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode(),
        "Trial terminated", Instant.now().minus(2, ChronoUnit.HOURS));
    Observation oldTrachInvasiveStatus = createObservation(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode(),
        "Discontinue", Instant.now().minus(4, ChronoUnit.HOURS));
    Observation newTrachInvasiveStatus1 = createObservation(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode(),
        "Discontinue", Instant.now().minus(1, ChronoUnit.HOURS));

    Observation newEttInvasiveInit = createObservation(
        ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode(),
        "Yes", Instant.now().minus(1, ChronoUnit.HOURS));
    Observation newTrachInvasiveInit = createObservation(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_INITIATION.getCode(),
        "Yes", Instant.now().minus(3, ChronoUnit.HOURS));

    assertFalse(isVentilated(new Observations(Arrays.asList(unrelatedObservation)),
        icuAdmitTime, now));
    assertFalse(isVentilated(new Observations(Arrays.asList(unrelatedObservation,
        peepObservation)), icuAdmitTime, now));

    assertTrue(isVentilated(new Observations(Arrays.asList(newEttInvasiveStatus,
        oldTrachInvasiveStatus)), icuAdmitTime, now));
    assertTrue(isVentilated(
        new Observations(Arrays.asList(newTrachInvasiveStatus, oldTrachInvasiveStatus)),
        icuAdmitTime, now));

    assertTrue(isVentilated(
        new Observations(Arrays.asList(oldTrachInvasiveStatus, newEttInvasiveInit)),
        icuAdmitTime, now));
    assertFalse(isVentilated(new Observations(Arrays.asList(newTrachInvasiveStatus1,
        newTrachInvasiveInit)), icuAdmitTime, now));

    assertTrue(isVentilated(new Observations(Arrays.asList(oldEttInvasiveStatus,
        newEttInvasiveStatus2)), icuAdmitTime, now));
    assertTrue(!isVentilated(new Observations(Arrays.asList(oldEttInvasiveStatus,
        newEttInvasiveStatus3)), icuAdmitTime, now));
    assertTrue(isVentilated(new Observations(Arrays.asList(oldTrachInvasiveStatus,
        newTrachInvasiveStatus2)), icuAdmitTime, now));
    assertTrue(!isVentilated(new Observations(Arrays.asList(oldTrachInvasiveStatus,
        newTrachInvasiveStatus3)), icuAdmitTime, now));
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
