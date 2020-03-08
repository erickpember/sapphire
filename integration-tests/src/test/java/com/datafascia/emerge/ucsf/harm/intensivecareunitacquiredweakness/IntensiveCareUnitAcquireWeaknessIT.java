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
