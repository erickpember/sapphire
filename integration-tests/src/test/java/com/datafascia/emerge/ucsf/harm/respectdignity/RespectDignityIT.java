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
package com.datafascia.emerge.ucsf.harm.respectdignity;

import com.datafascia.emerge.ucsf.CareProvider;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceTestSupport;
import java.time.Instant;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests respect and dignity data is exported.
 */
@Test(singleThreaded = true)
public class RespectDignityIT extends HarmEvidenceTestSupport {

  @BeforeMethod
  public void admitPatient() throws Exception {
    processMessage("ADT_A01.hl7");
  }

  @AfterMethod
  public void deletePatient() throws Exception {
    deleteIngestedData();
  }

  @Test
  public void should_export_icu_attending() throws Exception {
    processMessage("icu-attending.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CareProvider careProvider =
        harmEvidence.getMedicalData().getRespectDignity().getIcuAttending();

    assertEquals(careProvider.getName(), "MICHAEL ANTHONY MATTHAY");
    assertEquals(careProvider.getAddedToCareTeam().toInstant().toString(), "2015-11-05T20:22:00Z");
    assertEquals(careProvider.getUpdateTime().toInstant(), Instant.now(clock));
  }

  @Test
  public void should_export_primary_care_attending() throws Exception {
    processMessage("primary-care-attending.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CareProvider careProvider =
        harmEvidence.getMedicalData().getRespectDignity().getPrimaryServiceAttending();

    assertEquals(careProvider.getName(), "BRADLEY A SHARPE");
    assertEquals(careProvider.getAddedToCareTeam().toInstant(), Instant.now(clock));
    assertEquals(careProvider.getUpdateTime().toInstant(), Instant.now(clock));
  }

  @Test
  public void should_export_clinical_nurse() throws Exception {
    processMessage("clinical-nurse.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CareProvider careProvider =
        harmEvidence.getMedicalData().getRespectDignity().getRN();

    assertEquals(careProvider.getName(), "MELANIE JANE ABRAMS");
    assertEquals(careProvider.getAddedToCareTeam().toInstant().toString(), "2015-11-05T20:22:00Z");
    assertEquals(careProvider.getUpdateTime().toInstant(), Instant.now(clock));
  }
}
