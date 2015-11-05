// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm.ventilatorassociatedevent;

import com.datafascia.common.persist.Id;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.TimestampedBoolean;
import com.datafascia.emerge.ucsf.TimestampedMaybe;
import com.datafascia.emerge.ucsf.VentilationMode;
import com.datafascia.etl.harm.HarmEvidenceTestSupport;
import java.time.Instant;
import java.util.Date;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Tests ventilator associated event data is exported.
 */
@Test(singleThreaded = true)
public class VentilatorAssociatedEventIT extends HarmEvidenceTestSupport {

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

    TimestampedMaybe subglotticSuctionSurgicalAirway =
        harmEvidence.getMedicalData().getVAE().getSubglotticSuctionSurgicalAirway();
    assertEquals(subglotticSuctionSurgicalAirway.getValue(), TimestampedMaybe.Value.NO);
    assertEquals(subglotticSuctionSurgicalAirway.getUpdateTime(), Date.from(Instant.now(clock)));

    TimestampedMaybe subglotticSuctionNonSurgicalAirway =
        harmEvidence.getMedicalData().getVAE().getSubglotticSuctionNonSurgicalAirway();
    assertEquals(subglotticSuctionNonSurgicalAirway.getValue(), TimestampedMaybe.Value.NO);
    assertEquals(subglotticSuctionNonSurgicalAirway.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_ventilated() throws Exception {
    processMessage("ventilated-true.hl7");

    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    TimestampedBoolean ventilated = harmEvidence.getMedicalData().getVAE().getVentilated();
    assertTrue(ventilated.isValue());
    assertEquals(ventilated.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_discrete_hob_greater_than_30_deg_yes() throws Exception {
    processMessage("discrete-hob-greater-than-30-deg-yes.hl7");

    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    TimestampedMaybe headOfBed = harmEvidence.getMedicalData()
        .getVAE()
        .getDiscreteHOBGreaterThan30Deg();
    assertEquals(headOfBed.getValue(), TimestampedMaybe.Value.YES);
    assertEquals(headOfBed.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_ventilation_mode() throws Exception {
    processMessage("ventilation-mode-volume-control-ac.hl7");

    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    VentilationMode ventilationMode = harmEvidence.getMedicalData().getVAE().getVentilationMode();
    assertEquals(ventilationMode.getValue().toString(), "Volume Control (AC)");
    assertEquals(ventilationMode.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_subglottic_suction_non_surgical_airway() throws Exception {
    processMessage("subglottic-suction-non-surgical-airway-true.hl7");

    HarmEvidence harmEvidence = harmEvidenceRepository.read(Id.of(PATIENT_IDENTIFIER)).get();
    TimestampedMaybe subglotticSuctionNonSurgicalAirway =
        harmEvidence.getMedicalData().getVAE().getSubglotticSuctionNonSurgicalAirway();
    assertEquals(subglotticSuctionNonSurgicalAirway.getValue(), TimestampedMaybe.Value.YES);
    assertEquals(subglotticSuctionNonSurgicalAirway.getUpdateTime(), Date.from(Instant.now(clock)));
  }
}
