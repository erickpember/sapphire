// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm.alignmentofgoals;

import com.datafascia.common.persist.Id;
import com.datafascia.emerge.ucsf.ADPOLST;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.etl.harm.HarmEvidenceTestSupport;
import java.time.Instant;
import java.util.Date;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests alignment of goals data is exported.
 */
@Test(singleThreaded = true)
public class AlignmentOfGoalsIT extends HarmEvidenceTestSupport {

  @BeforeMethod
  public void admitPatient() throws Exception {
    processMessage("ADT_A01.hl7");
  }

  @AfterMethod
  public void dischargePatient() throws Exception {
    processMessage("ADT_A03.hl7");
  }

  @Test
  public void should_export_advance_directive() throws Exception {
    processMessage("advance-directive.hl7");

    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    ADPOLST adpolst = harmEvidence.getMedicalData().getAOG().getADPOLST();
    assertTrue(adpolst.isAdValue());
    assertEquals(adpolst.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_physician_orders_for_life_sustaining_treatment() throws Exception {
    processMessage("physician-orders-for-life-sustaining-treatment.hl7");

    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    ADPOLST adpolst = harmEvidence.getMedicalData().getAOG().getADPOLST();
    assertTrue(adpolst.isPolstValue());
    assertEquals(adpolst.getUpdateTime(), Date.from(Instant.now(clock)));
  }
}
