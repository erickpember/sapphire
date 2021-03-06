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
package com.datafascia.emerge.ucsf.harm.demographic;

import com.datafascia.emerge.ucsf.DemographicData;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceTestSupport;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests demographic data is exported.
 */
@Test(singleThreaded = true)
public class DemographicIT extends HarmEvidenceTestSupport {

  @AfterMethod
  public void deletePatient() throws Exception {
    deleteIngestedData();
  }

  @Test
  public void admit_should_export_demographic_data() throws Exception {
    processMessage("ADT_A01.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    DemographicData demographicData = harmEvidence.getDemographicData();

    assertEquals(demographicData.getPatientName(), "ONE A NATUS-ADULT");
    assertEquals(demographicData.getMedicalRecordNumber(), PATIENT_IDENTIFIER);
    assertEquals(demographicData.getICUadmitDate().toInstant().toString(), "2014-10-01T19:01:00Z");
    assertEquals(demographicData.getDateOfBirth(), "1984-10-01");
    assertEquals(demographicData.getGender(), DemographicData.Gender.FEMALE);
    assertEquals(demographicData.getRace(), "White");
    assertEquals(demographicData.getAdmissionHeight(), new BigDecimal("183"));
    assertEquals(demographicData.getAdmissionWeight(), new BigDecimal("100.0"));
    assertEquals(demographicData.getRoomNumber(), "0009-A");
    assertEquals(demographicData.getUpdateTime().toInstant(), Instant.now(clock));
  }

  @Test
  public void transfer_should_export_demographic_data() throws Exception {
    saveMessage("ADT_A01.hl7");
    processMessage("ADT_A02.hl7");
    processMessage("transfer-within-icu.hl7");
    processMessage("ADT_A08.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    DemographicData demographicData = harmEvidence.getDemographicData();

    assertEquals(demographicData.getPatientName(), "ONE A NATUS-ADULT");
    assertEquals(demographicData.getMedicalRecordNumber(), PATIENT_IDENTIFIER);
    assertEquals(demographicData.getICUadmitDate().toInstant().toString(), "2015-10-01T19:05:32Z");
    assertEquals(demographicData.getDateOfBirth(), "1999-10-01");
    assertEquals(demographicData.getGender(), DemographicData.Gender.FEMALE);
    assertEquals(demographicData.getRace(), "White");
    assertEquals(demographicData.getAdmissionHeight(), new BigDecimal("183"));
    assertEquals(demographicData.getAdmissionWeight(), new BigDecimal("100.0"));
    assertEquals(demographicData.getRoomNumber(), "0009-A");
    assertEquals(demographicData.getUpdateTime().toInstant(), Instant.now(clock));
  }

  @Test
  public void update_should_export_demographic_data() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("ADT_A08.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    DemographicData demographicData = harmEvidence.getDemographicData();

    assertEquals(demographicData.getPatientName(), "ONE A NATUS-ADULT");
    assertEquals(demographicData.getMedicalRecordNumber(), PATIENT_IDENTIFIER);
    assertEquals(demographicData.getICUadmitDate().toInstant().toString(), "2014-10-01T19:01:00Z");
    assertEquals(demographicData.getDateOfBirth(), "1999-10-01");
    assertEquals(demographicData.getGender(), DemographicData.Gender.FEMALE);
    assertEquals(demographicData.getRace(), "White");
    assertEquals(demographicData.getAdmissionHeight(), new BigDecimal("183"));
    assertEquals(demographicData.getAdmissionWeight(), new BigDecimal("100.0"));
    assertEquals(demographicData.getRoomNumber(), "0009-A");
    assertEquals(demographicData.getUpdateTime().toInstant(), Instant.now(clock));
  }

  @Test
  public void should_delete_harm_evidence() throws Exception {
    processMessage("ADT_A01.hl7");

    Optional<HarmEvidence> harmEvidence = readOptionalHarmEvidence();
    assertTrue(harmEvidence.isPresent());

    processMessage("ADT_A03.hl7");

    harmEvidence = readOptionalHarmEvidence();
    assertFalse(harmEvidence.isPresent());
  }

  @Test
  public void should_export_clinical_height_weight() throws Exception {
    processMessage("ADT_A01.hl7");
    processMessage("height-weight.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    DemographicData demographicData = harmEvidence.getDemographicData();

    assertEquals(demographicData.getAdmissionHeight(), new BigDecimal("185"));
    assertEquals(demographicData.getAdmissionWeight(), new BigDecimal("101"));

    processMessage("ADT_A03.hl7");
  }

  @Test
  public void should_export_height_weight_absent() throws Exception {
    processMessage("height-weight-absent.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    DemographicData demographicData = harmEvidence.getDemographicData();

    assertEquals(demographicData.getAdmissionHeight(), new BigDecimal(-1));
    assertEquals(demographicData.getAdmissionWeight(), new BigDecimal(-1));

    processMessage("ADT_A03.hl7");
  }
}
