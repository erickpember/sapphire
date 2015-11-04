// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm.paindelirium;

import com.datafascia.common.persist.Id;
import com.datafascia.emerge.ucsf.CurrentScore;
import com.datafascia.emerge.ucsf.CurrentScore___;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.Numerical;
import com.datafascia.emerge.ucsf.RASS;
import com.datafascia.etl.harm.HarmEvidenceTestSupport;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests pain and delirium data is exported.
 */
@Test(singleThreaded = true)
public class PainDeliriumIT extends HarmEvidenceTestSupport {

  @BeforeMethod
  public void admitPatient() throws Exception {
    processMessage("ADT_A01.hl7");
  }

  @AfterMethod
  public void dischargePatient() throws Exception {
    processMessage("ADT_A03.hl7");
  }

  @Test
  public void should_export_numerical_pain_8() throws Exception {
    processMessage("numerical-pain-8.hl7");
    processTimer();

    Id<HarmEvidence> patientId = Id.of(PATIENT_IDENTIFIER);
    HarmEvidence harmEvidence = harmEvidenceRepository.read(patientId).get();
    Numerical numerical =
        harmEvidence.getMedicalData().getDelirium().getPain().getNumerical();

    CurrentScore currentScore = numerical.getCurrentScore();
    assertEquals(currentScore.getPainScore(), 8);
    assertEquals(
        currentScore.getTimeOfDataAquisition().toInstant().toString(), "2014-09-29T21:48:59Z");
  }

  @Test
  public void should_export_rass_3() throws Exception {
    processMessage("rass-3.hl7");
    processTimer();

    Id<HarmEvidence> patientId = Id.of(PATIENT_IDENTIFIER);
    HarmEvidence harmEvidence = harmEvidenceRepository.read(patientId).get();
    RASS rass = harmEvidence.getMedicalData().getDelirium().getRASS();

    CurrentScore___ currentScore = rass.getCurrentScore();
    assertEquals(currentScore.getRASSScore(), 3);
    assertEquals(
        currentScore.getTimeOfDataAquisition().toInstant().toString(), "2014-09-29T21:48:59Z");
  }
}
