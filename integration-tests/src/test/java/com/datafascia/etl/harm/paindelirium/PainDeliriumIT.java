// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm.paindelirium;

import com.datafascia.emerge.ucsf.Cam;
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
import static org.testng.Assert.assertNull;

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
  public void deletePatient() {
    deleteIngestedData();
  }

  @Test
  public void should_export_numerical_pain_8() throws Exception {
    processMessage("numerical-pain-8.hl7");
    processTimer();

    HarmEvidence harmEvidence = harmEvidenceRepository.read(HARM_EVIDENCE_ID).get();
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

    HarmEvidence harmEvidence = harmEvidenceRepository.read(HARM_EVIDENCE_ID).get();
    RASS rass = harmEvidence.getMedicalData().getDelirium().getRASS();

    CurrentScore___ currentScore = rass.getCurrentScore();
    assertEquals(currentScore.getRASSScore(), 3);
    assertEquals(
        currentScore.getTimeOfDataAquisition().toInstant().toString(), "2014-09-29T21:48:59Z");
  }

  @Test
  public void should_export_cam_all() throws Exception {
    HarmEvidence harmEvidence;
    Cam cam;

    processMessage("cam-uta.hl7");
    processTimer();

    harmEvidence = harmEvidenceRepository.read(HARM_EVIDENCE_ID).get();
    cam = harmEvidence.getMedicalData().getDelirium().getCam();

    assertEquals(cam.getResult(), Cam.Result.UTA);
    assertEquals(cam.getUtaReason(), Cam.UtaReason.RASS_SCORE_4_OR_5);

    processMessage("cam-positive.hl7");
    processTimer();

    harmEvidence = harmEvidenceRepository.read(HARM_EVIDENCE_ID).get();
    cam = harmEvidence.getMedicalData().getDelirium().getCam();

    assertEquals(cam.getResult(), Cam.Result.POSITIVE);
    assertNull(cam.getUtaReason());

    processMessage("cam-negative.hl7");
    processTimer();

    harmEvidence = harmEvidenceRepository.read(HARM_EVIDENCE_ID).get();
    cam = harmEvidence.getMedicalData().getDelirium().getCam();

    assertEquals(cam.getResult(), Cam.Result.NEGATIVE);
    assertNull(cam.getUtaReason());
  }
}
