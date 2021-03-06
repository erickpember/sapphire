// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.emerge.ucsf.harm.paindelirium;

import com.datafascia.emerge.ucsf.Cam;
import com.datafascia.emerge.ucsf.CurrentScore;
import com.datafascia.emerge.ucsf.CurrentScore_;
import com.datafascia.emerge.ucsf.CurrentScore___;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.Numerical;
import com.datafascia.emerge.ucsf.PainGoal;
import com.datafascia.emerge.ucsf.RASS;
import com.datafascia.emerge.ucsf.RassGoal;
import com.datafascia.emerge.ucsf.Verbal;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceTestSupport;
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
  public void should_export_default_pain_goal() throws Exception {
    processMessage("cpot-8.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    PainGoal goal = harmEvidence.getMedicalData().getDelirium().getPain().getPainGoal();

    assertEquals(goal.getGoal(), 11);
    assertEquals(
        goal.getDataEntryTime().toInstant().toString(), "2014-09-29T23:48:59Z");
  }

  @Test
  public void should_export_cpot_pain_goal_8_1() throws Exception {
    processMessage("pain-goal.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    PainGoal goal = harmEvidence.getMedicalData().getDelirium().getPain().getPainGoal();

    assertEquals(goal.getGoal(), 8);
    assertEquals(
        goal.getDataEntryTime().toInstant().toString(), "2014-09-29T23:48:59Z");
  }

  @Test
  public void should_export_numerical_pain_8() throws Exception {
    processMessage("numerical-pain-8.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    Numerical numerical = harmEvidence.getMedicalData().getDelirium().getPain().getNumerical();

    CurrentScore currentScore = numerical.getCurrentScore();
    assertEquals(currentScore.getPainScore(), 8);
    assertEquals(
        currentScore.getTimeOfDataAquisition().toInstant().toString(), "2014-09-29T21:48:59Z");
  }

  @Test
  public void should_export_verbal_none() throws Exception {
    processMessage("verbal-pain-none.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    Verbal verbal = harmEvidence.getMedicalData().getDelirium().getPain().getVerbal();

    CurrentScore_ currentScore = verbal.getCurrentScore();
    assertEquals(currentScore.getPainScore(), 0);
    assertEquals(
        currentScore.getTimeOfDataAquisition().toInstant().toString(), "2014-09-29T21:50:59Z");
  }

  @Test
  public void should_export_verbal_mild() throws Exception {
    processMessage("verbal-pain-mild.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    Verbal verbal = harmEvidence.getMedicalData().getDelirium().getPain().getVerbal();

    CurrentScore_ currentScore = verbal.getCurrentScore();
    assertEquals(currentScore.getPainScore(), 1);
    assertEquals(
        currentScore.getTimeOfDataAquisition().toInstant().toString(), "2014-09-29T21:50:59Z");
  }

  @Test
  public void should_export_verbal_moderate() throws Exception {
    processMessage("verbal-pain-moderate.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    Verbal verbal = harmEvidence.getMedicalData().getDelirium().getPain().getVerbal();

    CurrentScore_ currentScore = verbal.getCurrentScore();
    assertEquals(currentScore.getPainScore(), 5);
    assertEquals(
        currentScore.getTimeOfDataAquisition().toInstant().toString(), "2014-09-29T21:50:59Z");
  }

  @Test
  public void should_export_verbal_severe() throws Exception {
    processMessage("verbal-pain-severe.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    Verbal verbal = harmEvidence.getMedicalData().getDelirium().getPain().getVerbal();

    CurrentScore_ currentScore = verbal.getCurrentScore();
    assertEquals(currentScore.getPainScore(), 7);
    assertEquals(
        currentScore.getTimeOfDataAquisition().toInstant().toString(), "2014-09-29T21:50:59Z");
  }

  @Test
  public void should_export_pain_goal_8() throws Exception {
    processMessage("numerical-pain-8.hl7");
    processMessage("pain-goal.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    PainGoal goal = harmEvidence.getMedicalData().getDelirium().getPain().getPainGoal();

    assertEquals(goal.getGoal(), 8);
    assertEquals(
        goal.getDataEntryTime().toInstant().toString(), "2014-09-29T23:48:59Z");
  }

  @Test
  public void should_export_cpot_pain_goal_8_2() throws Exception {
    processMessage("numerical-pain-8.hl7");
    processMessage("pain-goal.hl7");
    processMessage("cpot-8.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    PainGoal goal = harmEvidence.getMedicalData().getDelirium().getPain().getPainGoal();

    assertEquals(goal.getGoal(), 8);
    assertEquals(
        goal.getDataEntryTime().toInstant().toString(), "2014-09-29T23:48:59Z");
  }

  @Test
  public void should_export_pain_goal_no_pain() throws Exception {
    processMessage("numerical-pain-8.hl7");
    processMessage("pain-goal-no-pain.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    PainGoal goal = harmEvidence.getMedicalData().getDelirium().getPain().getPainGoal();

    assertEquals(goal.getGoal(), 0);
    assertEquals(
        goal.getDataEntryTime().toInstant().toString(), "2014-09-29T23:48:59Z");
  }

  @Test
  public void should_export_pain_goal_other() throws Exception {
    processMessage("numerical-pain-8.hl7");
    processMessage("pain-goal-other.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    PainGoal goal = harmEvidence.getMedicalData().getDelirium().getPain().getPainGoal();

    assertEquals(goal.getGoal(), 11);
    assertEquals(
        goal.getDataEntryTime().toInstant().toString(), "2014-09-29T23:48:59Z");
  }

  @Test
  public void should_export_rass_3() throws Exception {
    processMessage("rass-3.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    RASS rass = harmEvidence.getMedicalData().getDelirium().getRASS();

    CurrentScore___ currentScore = rass.getCurrentScore();
    assertEquals(currentScore.getRASSScore(), 3);
    assertEquals(
        currentScore.getTimeOfDataAquisition().toInstant().toString(), "2014-09-29T21:48:59Z");
  }

  @Test
  public void should_export_rass_goal_2() throws Exception {
    processNursingOrder("rass-goal-2.json");
    Thread.sleep(1000);

    HarmEvidence harmEvidence = readHarmEvidence();
    RASS rass = harmEvidence.getMedicalData().getDelirium().getRASS();

    RassGoal rassGoal = rass.getRassGoal();
    assertEquals(rassGoal.getGoal(), -2);
  }

  @Test
  public void should_export_cam_all() throws Exception {
    HarmEvidence harmEvidence;
    Cam cam;

    processMessage("cam-uta.hl7");
    processTimer();

    harmEvidence = readHarmEvidence();
    cam = harmEvidence.getMedicalData().getDelirium().getCam();

    assertEquals(cam.getResult(), Cam.Result.UTA);
    assertEquals(cam.getUtaReason(), Cam.UtaReason.RASS_SCORE_4_OR_5);

    processMessage("cam-positive.hl7");
    processTimer();

    harmEvidence = readHarmEvidence();
    cam = harmEvidence.getMedicalData().getDelirium().getCam();

    assertEquals(cam.getResult(), Cam.Result.POSITIVE);
    assertNull(cam.getUtaReason());

    processMessage("cam-negative.hl7");
    processTimer();

    harmEvidence = readHarmEvidence();
    cam = harmEvidence.getMedicalData().getDelirium().getCam();

    assertEquals(cam.getResult(), Cam.Result.NEGATIVE);
    assertNull(cam.getUtaReason());
  }
}
