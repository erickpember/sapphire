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
 * {@link MechanicalVentilationGreaterThan48Hours} test
 */
public class MechanicalVentilationGreaterThan48HoursTest
    extends MechanicalVentilationGreaterThan48Hours {

  public MechanicalVentilationGreaterThan48HoursTest() {
  }

  /**
   * Test of isMechanicalVentilationGreaterThan48Hours method,
   * of class MechanicalVentilationGreaterThan48Hours.
   * Does not include testing for Ventilated.
   */
  @Test
  public void testIsMechanicalVentilationGreaterThan48Hours_Observations_Instant() {
    Instant fortySevenHoursAgo = Instant.now().minus(47, ChronoUnit.HOURS);
    Instant fortyNineHoursAgo = Instant.now().minus(49, ChronoUnit.HOURS);

    Observation newInitiateInvasive = createObservation(
        ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode(),
        "Yes", fortySevenHoursAgo);

    assertFalse(isMechanicalVentilationGreaterThan48Hours(new Observations(Arrays.asList(
        newInitiateInvasive)), Instant.now()));

    Observation oldInitiateInvasive = createObservation(
        ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode(),
        "Yes", fortyNineHoursAgo);

    assertTrue(isMechanicalVentilationGreaterThan48Hours(new Observations(Arrays.asList(
        oldInitiateInvasive)), Instant.now()));

    Observation oldEttInvasiveStatus = createObservation(
        ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(),
        "Patient back on Invasive", fortyNineHoursAgo);

    assertTrue(isMechanicalVentilationGreaterThan48Hours(new Observations(Arrays.asList(
        oldEttInvasiveStatus)), Instant.now()));

    Observation newEttInvasiveStatus = createObservation(
        ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(),
        "Discontinue", fortySevenHoursAgo);

    assertFalse(isMechanicalVentilationGreaterThan48Hours(new Observations(Arrays.asList(
        oldEttInvasiveStatus, newEttInvasiveStatus)), Instant.now()));
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
