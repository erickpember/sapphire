// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm.respectdignity;

import com.datafascia.common.persist.Id;
import com.datafascia.emerge.ucsf.CareProvider;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.etl.harm.HarmEvidenceTestSupport;
import java.time.Instant;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests respect and dignity data is exported.
 */
@Test(singleThreaded = true)
public class RespectDignityIT extends HarmEvidenceTestSupport {

  @Test
  public void should_export_icu_attending() throws Exception {
    processMessage("icu-attending.hl7");

    Id<HarmEvidence> patientId = Id.of(PATIENT_IDENTIFIER);
    HarmEvidence harmEvidence = harmEvidenceRepository.read(patientId).get();
    CareProvider careProvider =
        harmEvidence.getMedicalData().getRespectDignity().getIcuAttending();

    assertEquals(careProvider.getName(), "DANIEL ELI ROTH");
    assertEquals(careProvider.getAddedToCareTeam().toInstant().toString(), "2014-10-01T07:00:00Z");
    assertEquals(careProvider.getUpdateTime().toInstant(), Instant.now(clock));
  }

  @Test
  public void should_export_primary_attending() throws Exception {
    processMessage("primary-attending.hl7");

    Id<HarmEvidence> patientId = Id.of(PATIENT_IDENTIFIER);
    HarmEvidence harmEvidence = harmEvidenceRepository.read(patientId).get();
    CareProvider careProvider =
        harmEvidence.getMedicalData().getRespectDignity().getPrimaryServiceAttending();

    assertEquals(careProvider.getName(), "Leanne Rorish");
    assertEquals(careProvider.getAddedToCareTeam().toInstant().toString(), "2014-10-01T07:00:00Z");
    assertEquals(careProvider.getUpdateTime().toInstant(), Instant.now(clock));
  }

  @Test
  public void should_export_clinical_nurse() throws Exception {
    processMessage("clinical-nurse.hl7");

    Id<HarmEvidence> patientId = Id.of(PATIENT_IDENTIFIER);
    HarmEvidence harmEvidence = harmEvidenceRepository.read(patientId).get();
    CareProvider careProvider =
        harmEvidence.getMedicalData().getRespectDignity().getRN();

    assertEquals(careProvider.getName(), "Jesse Salander");
    assertEquals(careProvider.getAddedToCareTeam().toInstant().toString(), "2014-10-01T07:00:00Z");
    assertEquals(careProvider.getUpdateTime().toInstant(), Instant.now(clock));
  }
}
