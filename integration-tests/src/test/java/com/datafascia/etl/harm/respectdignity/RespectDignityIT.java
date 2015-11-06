// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm.respectdignity;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.common.persist.Id;
import com.datafascia.common.persist.entity.EntityId;
import com.datafascia.domain.fhir.UnitedStatesPatient;
import com.datafascia.emerge.ucsf.CareProvider;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.etl.harm.HarmEvidenceTestSupport;
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

  private static final Id<UnitedStatesPatient> PATIENT_ID = Id.of("96093233");
  private static final Id<Encounter> ENCOUNTER_ID = Id.of("2088442");

  @BeforeMethod
  public void admitPatient() throws Exception {
    processMessage("ADT_A01.hl7");
  }

  @AfterMethod
  public void deletePatient() throws Exception {
    entityStore.delete(new EntityId(Encounter.class, ENCOUNTER_ID));
    entityStore.delete(new EntityId(UnitedStatesPatient.class, PATIENT_ID));
  }

  @Test
  public void should_export_icu_attending() throws Exception {
    processMessage("icu-attending.hl7");

    Id<HarmEvidence> patientId = Id.of(PATIENT_ID.toString());
    HarmEvidence harmEvidence = harmEvidenceRepository.read(patientId).get();
    CareProvider careProvider =
        harmEvidence.getMedicalData().getRespectDignity().getIcuAttending();

    assertEquals(careProvider.getName(), "MICHAEL ANTHONY MATTHAY");
    assertEquals(careProvider.getAddedToCareTeam().toInstant().toString(), "2015-11-05T20:22:00Z");
    assertEquals(careProvider.getUpdateTime().toInstant(), Instant.now(clock));
  }

  @Test
  public void should_export_primary_care_attending() throws Exception {
    processMessage("primary-care-attending.hl7");

    Id<HarmEvidence> patientId = Id.of(PATIENT_ID.toString());
    HarmEvidence harmEvidence = harmEvidenceRepository.read(patientId).get();
    CareProvider careProvider =
        harmEvidence.getMedicalData().getRespectDignity().getPrimaryServiceAttending();

    assertEquals(careProvider.getName(), "Leanne Rorish");
    assertEquals(careProvider.getAddedToCareTeam().toInstant().toString(), "2015-11-05T20:22:00Z");
    assertEquals(careProvider.getUpdateTime().toInstant(), Instant.now(clock));
  }

  @Test
  public void should_export_clinical_nurse() throws Exception {
    processMessage("clinical-nurse.hl7");

    Id<HarmEvidence> patientId = Id.of(PATIENT_ID.toString());
    HarmEvidence harmEvidence = harmEvidenceRepository.read(patientId).get();
    CareProvider careProvider =
        harmEvidence.getMedicalData().getRespectDignity().getRN();

    assertEquals(careProvider.getName(), "MELANIE JANE ABRAMS");
    assertEquals(careProvider.getAddedToCareTeam().toInstant().toString(), "2015-11-05T20:22:00Z");
    assertEquals(careProvider.getUpdateTime().toInstant(), Instant.now(clock));
  }
}
