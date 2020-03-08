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
package com.datafascia.emerge.ucsf.harm.alignmentofgoals;

import com.datafascia.emerge.ucsf.ADPOLST;
import com.datafascia.emerge.ucsf.AOG;
import com.datafascia.emerge.ucsf.CodeStatus;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.PatientCareConferenceNote;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceTestSupport;
import java.time.Instant;
import java.util.Date;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
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
  public void deletePatient() throws Exception {
    deleteIngestedData();
  }

  @Test
  public void should_export_default_values() {
    HarmEvidence harmEvidence = readHarmEvidence();

    AOG aog = harmEvidence.getMedicalData().getAOG();

    ADPOLST adpolst = aog.getADPOLST();
    assertFalse(adpolst.isAdValue());
    assertFalse(adpolst.isPolstValue());
    assertEquals(adpolst.getUpdateTime(), Date.from(Instant.now(clock)));

    PatientCareConferenceNote note = aog.getPatientCareConferenceNote();
    assertFalse(note.isValue());
    assertEquals(note.getUpdateTime(), Date.from(Instant.now(clock)));

    CodeStatus codeStatus = aog.getCodeStatus();
    assertEquals(codeStatus.getValue(), CodeStatus.Value.NO_CURRENT_CODE_STATUS);
  }

  @Test
  public void should_export_advance_directive() throws Exception {
    processMessage("advance-directive.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    ADPOLST adpolst = harmEvidence.getMedicalData().getAOG().getADPOLST();
    assertTrue(adpolst.isAdValue());
    assertEquals(adpolst.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_patient_care_conference_note() throws Exception {
    processMessage("patient-care-conference-note.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    PatientCareConferenceNote note =
        harmEvidence.getMedicalData().getAOG().getPatientCareConferenceNote();
    assertTrue(note.isValue());
    assertEquals(
        note.getPatientCareConferenceNoteTime().toInstant().toString(), "2015-02-26T22:22:25Z");
    assertEquals(note.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_physician_orders_for_life_sustaining_treatment() throws Exception {
    processMessage("physician-orders-for-life-sustaining-treatment.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    ADPOLST adpolst = harmEvidence.getMedicalData().getAOG().getADPOLST();
    assertTrue(adpolst.isPolstValue());
    assertEquals(adpolst.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_code_status_attending_dnr_dni() throws Exception {
    processNursingOrder("code-status-attending-dnr-dni.json");

    HarmEvidence harmEvidence = readHarmEvidence();
    CodeStatus codeStatus = harmEvidence.getMedicalData().getAOG().getCodeStatus();
    assertEquals(codeStatus.getValue(), CodeStatus.Value.NO_CURRENT_CODE_STATUS);

    Thread.sleep(1000);
    processTimer();

    harmEvidence = readHarmEvidence();
    codeStatus = harmEvidence.getMedicalData().getAOG().getCodeStatus();
    assertEquals(codeStatus.getValue(), CodeStatus.Value.ATTENDING_DNR_DNI);
    assertEquals(codeStatus.getUpdateTime(), Date.from(Instant.now(clock)));
  }
}
