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
package com.datafascia.emerge.ucsf.harm.venousthromboembolism;

import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.PharmacologicVTEProphylaxis;
import com.datafascia.emerge.ucsf.SCDs;
import com.datafascia.emerge.ucsf.TimestampedBoolean;
import com.datafascia.emerge.ucsf.VTE;
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
 * Tests venous thromboembolism data is exported.
 */
@Test(singleThreaded = true)
public class VenousThromboembolismIT extends HarmEvidenceTestSupport {

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
    VTE vte = harmEvidence.getMedicalData().getVTE();
    PharmacologicVTEProphylaxis prophylaxis = vte.getPharmacologicVTEProphylaxis();

    TimestampedBoolean pharmacologicVTEProphylaxisOrdered = prophylaxis.getOrdered();
    assertFalse(pharmacologicVTEProphylaxisOrdered.isValue());
    assertEquals(pharmacologicVTEProphylaxisOrdered.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_scds_in_use_false() throws Exception {
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    SCDs scds = harmEvidence.getMedicalData().getVTE().getSCDs();

    TimestampedBoolean scdsInUse = scds.getInUse();
    assertFalse(scdsInUse.isValue());
    assertEquals(scdsInUse.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_scds_in_use_true() throws Exception {
    processMessage("sequential-compression-devices.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    SCDs scds = harmEvidence.getMedicalData().getVTE().getSCDs();

    TimestampedBoolean scdsInUse = scds.getInUse();
    assertTrue(scdsInUse.isValue());
    assertEquals(scdsInUse.getUpdateTime(), Date.from(Instant.now(clock)));
  }
}
