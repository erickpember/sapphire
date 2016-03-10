// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.MedicationAdministration;
import ca.uhn.fhir.model.dstu2.valueset.MedicationAdministrationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.testUtils.TestResources;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Date;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests Daily Sedation Interruption Candidate logic without the API.
 */
public class DailySedationInterruptionCandidateTest extends DailySedationInterruptionCandidate {

  public DailySedationInterruptionCandidateTest() {
  }

  /**
   * Test of getDailySedationInterruptionCandidate method
   */
  @Test
  public void testGetDailySedationInterruptionCandidate_4args() {

    ClientBuilder client = TestResources.createMockClient();

    MedicationAdministration completedPropofol = TestResources.createMedicationAdministration(
        "completedPropofol",
        Arrays.asList(MedsSetEnum.CONTINUOUS_INFUSION_PROPOFOL_IV.getCode(),
            MedsSetEnum.ANY_SEDATIVE_INFUSION.getCode()),
        MedicationAdministrationStatusEnum.IN_PROGRESS,
        1,
        "mg/kg",
        new DateTimeDt(Date.from(Instant.now().minus(15, ChronoUnit.HOURS))),
        "completedOrder");

    assertEquals(getDailySedationInterruptionCandidate(
        "encounterId",
        Arrays.asList(completedPropofol),
        Instant.now(),
        client), CandidateResult.OFF_SEDATION);

    MedicationAdministration activePropo = TestResources.createMedicationAdministration(
        "activePropo",
        Arrays.asList(MedsSetEnum.CONTINUOUS_INFUSION_PROPOFOL_IV.getCode(),
            MedsSetEnum.ANY_SEDATIVE_INFUSION.getCode()),
        MedicationAdministrationStatusEnum.IN_PROGRESS,
        1,
        "mg/kg",
        new DateTimeDt(Date.from(Instant.now().minus(15, ChronoUnit.HOURS))),
        "activeOrder");

    assertEquals(getDailySedationInterruptionCandidate(
        "encounterId",
        Arrays.asList(activePropo),
        Instant.now(),
        client), CandidateResult.YES);
  }

}
