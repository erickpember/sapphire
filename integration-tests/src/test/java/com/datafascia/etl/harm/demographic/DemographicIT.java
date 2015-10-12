// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm.demographic;

import com.datafascia.common.persist.Id;
import com.datafascia.emerge.ucsf.DemographicData;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.persist.HarmEvidenceRepository;
import com.datafascia.etl.harm.HarmEvidenceTestSupport;
import java.time.Clock;
import java.time.Instant;
import java.util.Optional;
import javax.inject.Inject;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests demographic data is exported.
 */
@Test(singleThreaded = true)
public class DemographicIT extends HarmEvidenceTestSupport {

  private static final String PATIENT_IDENTIFIER = "97546762";

  @Inject
  private Clock clock;

  @Inject
  private HarmEvidenceRepository harmEvidenceRepository;

  @Test
  public void should_export_demographic_data() throws Exception {
    processMessage("ADT_A01.hl7");

    Id<HarmEvidence> patientId = Id.of(PATIENT_IDENTIFIER);
    HarmEvidence harmEvidence = harmEvidenceRepository.read(patientId).get();
    DemographicData demographicData = harmEvidence.getDemographicData();

    assertEquals(demographicData.getPatientName(), "ONE A NATUS-ADULT");
    assertEquals(demographicData.getMedicalRecordNumber(), PATIENT_IDENTIFIER);
    assertEquals(demographicData.getICUadmitDate().toInstant().toString(), "2014-10-01T19:01:00Z");
    assertEquals(demographicData.getDateOfBirth(), "1984-10-01");
    assertEquals(demographicData.getGender(), DemographicData.Gender.FEMALE);
    assertEquals(demographicData.getRace(), "W");
    assertEquals(demographicData.getRoomNumber(), "A6597-28");
    assertEquals(demographicData.getUpdateTime().toInstant(), Instant.now(clock));
  }

  @Test
  public void should_delete_harm_evidence() throws Exception {
    processMessage("ADT_A01.hl7");

    Id<HarmEvidence> patientId = Id.of(PATIENT_IDENTIFIER);
    Optional<HarmEvidence> harmEvidence = harmEvidenceRepository.read(patientId);
    assertTrue(harmEvidence.isPresent());

    processMessage("ADT_A03.hl7");
    harmEvidence = harmEvidenceRepository.read(patientId);
    assertFalse(harmEvidence.isPresent());
  }
}