// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.vae.CurrentTidalVolume;
import com.datafascia.emerge.harms.vae.DiscreteHeadOfBedGreaterThan30Degrees;
import com.datafascia.emerge.harms.vae.MechanicalVentilationGreaterThan48Hours;
import com.datafascia.emerge.harms.vae.RecentStressUlcerProphylaxisAdministration;
import com.datafascia.emerge.harms.vae.StressUlcerProphylacticsOrder;
import com.datafascia.emerge.harms.vae.SubglotticSuctionUse;
import com.datafascia.emerge.harms.vae.Ventilated;
import com.datafascia.emerge.harms.vae.VentilationModeImpl;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.TimestampedBoolean;
import com.datafascia.emerge.ucsf.TimestampedInteger;
import com.datafascia.emerge.ucsf.TimestampedMaybe;
import com.datafascia.emerge.ucsf.VAE;
import com.datafascia.emerge.ucsf.VentilationMode;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import javax.inject.Inject;

/**
 * Updates ventilator associated event data for a patient.
 */
public class VentilatorAssociatedEventUpdater {

  @Inject
  private Clock clock;

  @Inject
  private Ventilated ventilatedImpl;

  @Inject
  private DiscreteHeadOfBedGreaterThan30Degrees discreteHeadOfBedGreaterThan30DegreesImpl;

  @Inject
  private StressUlcerProphylacticsOrder stressUlcerProphylacticsOrderImpl;

  @Inject
  private MechanicalVentilationGreaterThan48Hours mechanicalVentilationGreaterThan48HoursImpl;

  @Inject
  private RecentStressUlcerProphylaxisAdministration recentStressUlcerProphylaxisAdministration;

  @Inject
  private VentilationModeImpl ventilationModeImpl;

  @Inject
  private CurrentTidalVolume currentTidalVolume;

  @Inject
  private SubglotticSuctionUse subglotticSuctionUse;

  private static VAE getVAE(HarmEvidence harmEvidence) {
    MedicalData medicalData = harmEvidence.getMedicalData();
    VAE vae = medicalData.getVAE();
    if (vae == null) {
      vae = new VAE();
      medicalData.setVAE(vae);
    }

    return vae;
  }

  /**
   * Updates ventilated.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateVentilated(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    TimestampedBoolean ventilated = new TimestampedBoolean()
        .withValue(ventilatedImpl.isVentilated(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setVentilated(ventilated);
  }

  /**
   * Updates head of bed angle greater than or equal to 30 degrees.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateDiscreteHOBGreaterThan30Deg(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    String value = discreteHeadOfBedGreaterThan30DegreesImpl.getHeadOfBedGreaterThan30Degrees(
        encounterId).getCode();

    TimestampedMaybe hobGreaterThan30Deg = new TimestampedMaybe()
        .withValue(TimestampedMaybe.Value.fromValue(value))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setDiscreteHOBGreaterThan30Deg(hobGreaterThan30Deg);
  }

  /**
   * Updates stress ulcer prophylaxis order.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateStressUlcerProphylaxisOrder(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    TimestampedBoolean order = new TimestampedBoolean()
        .withValue(stressUlcerProphylacticsOrderImpl.haveStressUlcerProphylacticsOrder(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setSupOrderStatus(order);
  }

  /**
   * Updates mechanical ventilation greater than 48 hours.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateMechanicalVentilationGreaterThan48Hours(
      HarmEvidence harmEvidence, Encounter encounter) {

    String encounterId = encounter.getId().getIdPart();

    TimestampedBoolean mechanicalVentilation = new TimestampedBoolean()
        .withValue(
            mechanicalVentilationGreaterThan48HoursImpl.isMechanicalVentilationGreaterThan48Hours(
                encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setMechanicalVentilationGreaterThan48Hours(mechanicalVentilation);
  }

  /**
   * Updates recent stress ulcer prophylaxis administration.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateRecentStressUlcerProphylaxisAdministration(
      HarmEvidence harmEvidence, Encounter encounter) {

    String encounterId = encounter.getId().getIdPart();

    TimestampedBoolean administration = new TimestampedBoolean()
        .withValue(recentStressUlcerProphylaxisAdministration.test(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setRecentStressUlcerProphylaxisAdministration(administration);
  }

  /**
   * Updates ventilation mode.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateVentilationMode(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    String value = ventilationModeImpl.getVentilationMode(encounterId);

    VentilationMode ventilationMode = new VentilationMode()
        .withValue((value == null) ? null : VentilationMode.Value.fromValue(value))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setVentilationMode(ventilationMode);
  }

  /**
   * Updates current tidal volume.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateCurrentTidalVolume(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    TimestampedInteger tidalVolume = new TimestampedInteger()
        .withValue(currentTidalVolume.apply(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setCurrentTidalVolume(tidalVolume);
  }

  /**
   * Updates subglottic suction use.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateSubglotticSuctionUse(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    String value = subglotticSuctionUse.apply(encounterId).getCode();

    TimestampedMaybe newSubglotticSuctionUse = new TimestampedMaybe()
        .withValue(TimestampedMaybe.Value.fromValue(value))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setSubglotticSuctionUse(newSubglotticSuctionUse);
  }
}
