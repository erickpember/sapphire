// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
