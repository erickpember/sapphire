// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm.intensivecareunitacquiredweakness;

import com.datafascia.common.persist.Id;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MobilityAchieved;
import com.datafascia.etl.harm.HarmEvidenceTestSupport;
import java.time.Instant;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests intensive care unit acquired weakness data is exported.
 */
@Test(singleThreaded = true)
public class IntensiveCareUnitAcquireWeaknessIT extends HarmEvidenceTestSupport {

  @BeforeMethod
  public void admitPatient() throws Exception {
    processMessage("ADT_A01.hl7");
  }

  @AfterMethod
  public void dischargePatient() throws Exception {
    processMessage("ADT_A03.hl7");
  }

  @Test
  public void should_export_mobility_achieved() throws Exception {
    processMessage("mobility-achieved.hl7");

    Id<HarmEvidence> patientId = Id.of(PATIENT_IDENTIFIER);
    HarmEvidence harmEvidence = harmEvidenceRepository.read(patientId).get();
    MobilityAchieved mobilityAchieved =
        harmEvidence.getMedicalData().getIAW().getMobilityAchieved();

    assertEquals(mobilityAchieved.getLevelMobilityAchieved(), 2);
    assertEquals(
        mobilityAchieved.getMobilityScoreTime().toInstant().toString(), "2015-10-06T21:00:00Z");
    assertEquals(mobilityAchieved.getClinicianType(), MobilityAchieved.ClinicianType.RN);
    assertEquals(
        mobilityAchieved.getAssistDevice(), MobilityAchieved.AssistDevice.LATERAL_TRANSFER_DEVICE);
    assertEquals(mobilityAchieved.getNumberOfAssists(), MobilityAchieved.NumberOfAssists._2);
    assertEquals(mobilityAchieved.getUpdateTime().toInstant(), Instant.now(clock));
  }
}
