// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm.venousthromboembolism;

import com.datafascia.common.persist.Id;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.TimestampedBoolean;
import com.datafascia.emerge.ucsf.VTE;
import com.datafascia.etl.harm.HarmEvidenceTestSupport;
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
  public void dischargePatient() throws Exception {
    processMessage("ADT_A03.hl7");
  }

  @Test
  public void should_export_default_values() {
    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    VTE vte = harmEvidence.getMedicalData().getVTE();

    TimestampedBoolean pharmacologicVTEProphylaxisOrdered =
        vte.getPharmacologicVTEProphylaxisOrdered();
    assertFalse(pharmacologicVTEProphylaxisOrdered.isValue());
    assertEquals(pharmacologicVTEProphylaxisOrdered.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_scds_in_use_false() throws Exception {
    harmEvidenceUpdater.processTimer(getEncounter());

    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    TimestampedBoolean scdsInUse = harmEvidence.getMedicalData().getVTE().getSCDsInUse();
    assertFalse(scdsInUse.isValue());
    assertEquals(scdsInUse.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_scds_in_use_true() throws Exception {
    processMessage("sequential-compression-devices.hl7");
    harmEvidenceUpdater.processTimer(getEncounter());

    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    TimestampedBoolean scdsInUse = harmEvidence.getMedicalData().getVTE().getSCDsInUse();
    assertTrue(scdsInUse.isValue());
    assertEquals(scdsInUse.getUpdateTime(), Date.from(Instant.now(clock)));
  }
}
