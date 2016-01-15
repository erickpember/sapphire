// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.harm.intensivecareunitacquiredweakness;

import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.Mobility;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceTestSupport;
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

    HarmEvidence harmEvidence = readHarmEvidence();
    Mobility mobility = harmEvidence.getMedicalData().getIAW().getMobility().get(0);
    assertEquals(mobility.getLevelMobilityAchieved(), 2);
    assertEquals(
        mobility.getMobilityScoreTime().toInstant().toString(), "2014-09-29T21:51:42Z");
    assertEquals(mobility.getClinicianType(), Mobility.ClinicianType.RN);
    assertEquals(
        mobility.getAssistDevice(), Mobility.AssistDevice.LATERAL_TRANSFER_DEVICE);
    assertEquals(mobility.getNumberOfAssists(), Mobility.NumberOfAssists._2);
    assertEquals(mobility.getUpdateTime().toInstant(), Instant.now(clock));

    assertEquals(harmEvidence.getMedicalData().getIAW().getMobility().size(), 1);
  }
}
