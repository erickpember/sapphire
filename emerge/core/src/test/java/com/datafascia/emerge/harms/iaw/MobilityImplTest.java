// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.iaw;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.api.client.Observations;
import com.datafascia.domain.fhir.Dates;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.ZoneId;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * {@link MobilityImpl} test
 */
public class MobilityImplTest {

  /**
   * Test of getMobility method, of class MobilityImpl.
   */
  @Test
  public void testGetMobility_Observations() {
    MobilityImpl mobilityScore = new MobilityImpl();
    Observation obs1 = createObservation(
        "RN_1: Bed-chair position,1 person,0,0");
    Observation obs2 = createObservation(
        "RN_2: Tilt Table,Independent,Lateral transfer device,2 persons");
    Observation obs3 = createObservation(
        "OT_5: Active Transfer to Chair, any assist level");
    Observation obs4 = createObservation(
        "PT_4: Active Transfer to Chair with any level of assist");

    Observations observations = new Observations(Arrays.asList(obs1, obs2, obs3, obs4));

    Clock clock = Clock.fixed(Instant.now(), ZoneId.of("America/Los_Angeles"));
    List<MobilityImpl.MobilityScore> results = mobilityScore.getMobility(observations, clock);
    assertEquals(results.size(), 3);

    assertEquals(results.get(0).getClinicianType(), ClinicianTypeEnum.RN);
    assertEquals(results.get(0).getLevelMobilityAchieved(), 2);
    assertEquals(results.get(0).getMobilityScoreTime(), Dates.toDate(obs2.getEffective()));
    assertEquals(results.get(0).getNumberOfAssists(), NumberOfAssistsEnum.ONE);

    assertEquals(results.get(1).getClinicianType(), ClinicianTypeEnum.OT);
    assertEquals(results.get(1).getLevelMobilityAchieved(), 5);

    assertEquals(results.get(2).getClinicianType(), ClinicianTypeEnum.PT);
    assertEquals(results.get(2).getLevelMobilityAchieved(), 4);
  }

  private Observation createObservation(String value) {
    DateTimeDt effectiveTime = DateTimeDt.withCurrentTime();
    String code = ObservationCodeEnum.MOBILITY_SCORE.getCode();
    Observation observation = new Observation()
        .setCode(new CodeableConceptDt("system", code))
        .setValue(new StringDt(value))
        .setIssued(new Date(), TemporalPrecisionEnum.SECOND)
        .setEffective(effectiveTime);
    observation.setId(this.getClass().getSimpleName() + code);
    return observation;
  }
}
