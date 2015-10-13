// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm.centrallineassociatedbloodstreaminfection;

import com.datafascia.common.persist.Id;
import com.datafascia.emerge.ucsf.CLABSI;
import com.datafascia.emerge.ucsf.CentralLine;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.etl.harm.HarmEvidenceTestSupport;
import java.time.Instant;
import java.util.Date;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Tests central line associated blood stream infection data is exported.
 */
@Test(singleThreaded = true)
public class CentralLineAssociateBloodStreamInfectionIT extends HarmEvidenceTestSupport {

  @BeforeMethod
  public void admitPatient() throws Exception {
    processMessage("ADT_A01.hl7");
  }

  @AfterMethod
  public void dischargePatient() throws Exception {
    processMessage("ADT_A03.hl7");
  }

  @Test
  public void should_export_tunneled_cvc_single_lumen_femoral_left() throws Exception {
    processMessage("tunneled-cvc-single-lumen-femoral-left.hl7");

    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    CentralLine centralLine = clabsi.getCentralLine().get(0);
    assertEquals(centralLine.getType(), CentralLine.Type.TUNNELED_CVC_SINGLE_LUMEN);
    assertEquals(centralLine.getSite(), CentralLine.Site.FEMORAL);
    assertEquals(centralLine.getSide(), CentralLine.Side.LEFT);
    assertEquals(centralLine.getInsertionDate().toInstant().toString(), "2015-02-09T02:19:00Z");
    assertEquals(centralLine.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_not_export_removed_tunneled_cvc_single_lumen_femoral_left() throws Exception {
    processMessage("tunneled-cvc-single-lumen-femoral-left.hl7");
    processMessage("removed-tunneled-cvc-single-lumen-femoral-left.hl7");

    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    assertEquals(clabsi.getCentralLine().size(), 0);
  }
}