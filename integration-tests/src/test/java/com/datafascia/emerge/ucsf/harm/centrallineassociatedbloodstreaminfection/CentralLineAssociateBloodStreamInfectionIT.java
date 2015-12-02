// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.harm.centrallineassociatedbloodstreaminfection;

import com.datafascia.emerge.ucsf.CLABSI;
import com.datafascia.emerge.ucsf.CentralLine;
import com.datafascia.emerge.ucsf.DailyNeedsAssessment;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceTestSupport;
import java.time.Instant;
import java.util.Date;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.junit.Assert.assertNull;
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
  public void deletePatient() throws Exception {
    deleteIngestedData();
  }

  @Test
  public void should_export_daily_needs_assessment_yes() throws Exception {
    processMessage("tunneled-cvc-single-lumen-femoral-left.hl7");
    processMessage("daily-needs-assessment-yes.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    DailyNeedsAssessment dailyNeedsAssessment =
        harmEvidence.getMedicalData().getCLABSI().getDailyNeedsAssessment();
    assertEquals(dailyNeedsAssessment.getPerformed(), DailyNeedsAssessment.Performed.YES);
    assertEquals(dailyNeedsAssessment.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_introducer_femoral_right() throws Exception {
    processMessage("introducer-femoral-right.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    CentralLine centralLine = clabsi.getCentralLine().get(0);
    assertEquals(centralLine.getType(), CentralLine.Type.INTRODUCER);
    assertEquals(centralLine.getSite(), CentralLine.Site.FEMORAL);
    assertEquals(centralLine.getSide(), CentralLine.Side.RIGHT);
    assertNull(centralLine.getInsertionDate());
    assertEquals(centralLine.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_picc_double_lumen_arm_right() throws Exception {
    processMessage("picc-double-lumen-arm-right.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    CentralLine centralLine = clabsi.getCentralLine().get(0);
    assertEquals(centralLine.getType(), CentralLine.Type.PICC_DOUBLE_LUMEN);
    assertEquals(centralLine.getSite(), CentralLine.Site.UPPER_ARM);
    assertEquals(centralLine.getSide(), CentralLine.Side.RIGHT);
    assertEquals(centralLine.getInsertionDate().toInstant().toString(), "2015-11-05T16:57:00Z");
    assertEquals(centralLine.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_double_lumen_hemodialysis_pheresis_catheter_internal_jugular_left()
      throws Exception {

    processMessage("double-lumen-hemodialysis-pheresis-catheter-internal-jugular-left.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    CentralLine centralLine = clabsi.getCentralLine().get(0);
    assertEquals(
        centralLine.getType(), CentralLine.Type.DOUBLE_LUMEN_HEMODIALYSIS_PHERESIS_CATHETER);
    assertEquals(centralLine.getSite(), CentralLine.Site.INTERNAL_JUGULAR);
    assertEquals(centralLine.getSide(), CentralLine.Side.LEFT);
    assertEquals(centralLine.getInsertionDate().toInstant().toString(), "2015-11-05T16:57:00Z");
    assertEquals(centralLine.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_tunneled_cvc_single_lumen_femoral_left() throws Exception {
    processMessage("tunneled-cvc-single-lumen-femoral-left.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
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

    HarmEvidence harmEvidence = readHarmEvidence();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    assertEquals(clabsi.getCentralLine().size(), 0);
  }
}
