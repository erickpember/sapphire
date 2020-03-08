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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

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
  public void should_not_export_arteriovenous_fistula() throws Exception {
    processMessage("arteriovenous-fistula.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    assertNull(clabsi);
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
    assertEquals(centralLine.getInsertionDate().toInstant().toString(), "2015-12-27T08:00:00Z");
    assertEquals(centralLine.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_psi_introducer_other() throws Exception {
    processMessage("psi-introducer-other.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    CentralLine centralLine = clabsi.getCentralLine().get(0);
    assertEquals(centralLine.getType(), CentralLine.Type.PSI_INTRODUCER);
    assertEquals(centralLine.getSite(), CentralLine.Site.OTHER);
    assertEquals(centralLine.getSide(), CentralLine.Side.N_A);
    assertNull(centralLine.getInsertionDate());
    assertEquals(centralLine.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_implanted_port_double_lumen() throws Exception {
    processMessage("implanted-port-double-lumen.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    CentralLine centralLine = clabsi.getCentralLine().get(0);
    assertEquals(centralLine.getType(), CentralLine.Type.IMPLANTED_PORT_DOUBLE_LUMEN);
    assertEquals(centralLine.getSite(), CentralLine.Site.OTHER);
    assertEquals(centralLine.getSide(), CentralLine.Side.N_A);
    assertEquals(centralLine.getInsertionDate().toInstant().toString(),"2015-12-29T21:48:00Z");
    assertEquals(centralLine.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_not_export_removed_introducer_femoral_right() throws Exception {
    processMessage("introducer-femoral-right.hl7");
    processMessage("removed-introducer-femoral-right.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    assertEquals(clabsi.getCentralLine().size(), 0);
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
    processMessage("double-lumen-hemodialysis-pheresis-catheter-internal-jugular-left2.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    CLABSI clabsi = harmEvidence.getMedicalData().getCLABSI();
    CentralLine centralLine = clabsi.getCentralLine().get(0);
    assertEquals(
        centralLine.getType(), CentralLine.Type.DOUBLE_LUMEN_HEMODIALYSIS_PHERESIS_CATHETER);
    assertEquals(centralLine.getSite(), CentralLine.Site.INTERNAL_JUGULAR);
    assertEquals(centralLine.getSide(), CentralLine.Side.LEFT);
    assertEquals(centralLine.getInsertionDate().toInstant().toString(), "2016-01-10T16:57:00Z");
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
