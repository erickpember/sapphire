// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.harm.ventilatorassociatedevent;

import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.Mode;
import com.datafascia.emerge.ucsf.TimestampedBoolean;
import com.datafascia.emerge.ucsf.TimestampedMaybe;
import com.datafascia.emerge.ucsf.VAE;
import com.datafascia.emerge.ucsf.harm.HarmEvidenceTestSupport;
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
    HarmEvidence harmEvidence = readHarmEvidence();
    VAE vae = harmEvidence.getMedicalData().getVAE();

    TimestampedMaybe subglotticSuctionSurgicalAirway = vae.getSubglotticSuctionSurgicalAirway();
    assertEquals(
        subglotticSuctionSurgicalAirway.getValue(), TimestampedMaybe.Value.NO);
    assertEquals(subglotticSuctionSurgicalAirway.getUpdateTime(), Date.from(Instant.now(clock)));

    TimestampedMaybe subglotticSuctionNonSurgicalAirway =
        vae.getSubglotticSuctionNonSurgicalAirway();
    assertEquals(
        subglotticSuctionNonSurgicalAirway.getValue(), TimestampedMaybe.Value.NOT_DOCUMENTED);
    assertEquals(subglotticSuctionNonSurgicalAirway.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_ventilated() throws Exception {
    processMessage("ventilated-true.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    TimestampedBoolean ventilated =
        harmEvidence.getMedicalData().getVAE().getVentilation().getVentilated();
    assertTrue(ventilated.isValue());
    assertEquals(ventilated.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_discrete_hob_greater_than_30_deg_yes() throws Exception {
    processMessage("discrete-hob-greater-than-30-deg-yes.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    TimestampedMaybe headOfBed = harmEvidence.getMedicalData()
        .getVAE()
        .getDiscreteHOBGreaterThan30Deg();
    assertEquals(headOfBed.getValue(), TimestampedMaybe.Value.YES);
    assertEquals(headOfBed.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_ventilation_mode() throws Exception {
    processMessage("ventilation-mode-volume-control-ac.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    Mode mode = harmEvidence.getMedicalData().getVAE().getVentilation().getMode();
    assertEquals(mode.getValue().toString(), "Volume Control (AC)");
    assertEquals(mode.getUpdateTime(), Date.from(Instant.now(clock)));

    processMessage("ventilation-mode-aprv1.hl7");

    harmEvidence = readHarmEvidence();
    mode = harmEvidence.getMedicalData().getVAE().getVentilation().getMode();
    assertEquals(mode.getValue().toString(), "Airway Pressure Release Ventilation (APRV)");
    assertEquals(mode.getUpdateTime(), Date.from(Instant.now(clock)));

    processMessage("ventilation-mode-aprv2.hl7");

    harmEvidence = readHarmEvidence();
    mode = harmEvidence.getMedicalData().getVAE().getVentilation().getMode();
    assertEquals(mode.getValue().toString(), "Airway Pressure Release Ventilation (APRV)");
    assertEquals(mode.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_subglottic_suction_non_surgical_airway() throws Exception {
    processMessage("subglottic-suction-non-surgical-airway-true.hl7");

    HarmEvidence harmEvidence = readHarmEvidence();
    TimestampedMaybe subglotticSuctionNonSurgicalAirway =
        harmEvidence.getMedicalData().getVAE().getSubglotticSuctionNonSurgicalAirway();
    assertEquals(subglotticSuctionNonSurgicalAirway.getValue(), TimestampedMaybe.Value.YES);
    assertEquals(subglotticSuctionNonSurgicalAirway.getUpdateTime(), Date.from(Instant.now(clock)));
  }

  @Test
  public void should_export_inline_suction() throws Exception {
    processMessage("inline-suction-true.hl7");
    processTimer();

    HarmEvidence harmEvidence = readHarmEvidence();
    TimestampedBoolean inlineSuction = harmEvidence.getMedicalData().getVAE().getInlineSuction();
    assertTrue(inlineSuction.isValue());
    assertEquals(inlineSuction.getUpdateTime(), Date.from(Instant.now(clock)));
  }
}
