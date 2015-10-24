// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.vae.CurrentTidalVolume;
import com.datafascia.emerge.harms.vae.DailySedationInterruption;
import com.datafascia.emerge.harms.vae.DailySedationInterruptionCandidate;
import com.datafascia.emerge.harms.vae.DailySpontaneousBreathingTrialImpl;
import com.datafascia.emerge.harms.vae.DiscreteHeadOfBedGreaterThan30Degrees;
import com.datafascia.emerge.harms.vae.InlineSuction;
import com.datafascia.emerge.harms.vae.MechanicalVentilationGreaterThan48Hours;
import com.datafascia.emerge.harms.vae.OralCare;
import com.datafascia.emerge.harms.vae.RecentStressUlcerProphylaxisAdministration;
import com.datafascia.emerge.harms.vae.StressUlcerProphylacticsOrder;
import com.datafascia.emerge.harms.vae.SubglotticSuctionNonSurgicalAirway;
import com.datafascia.emerge.harms.vae.SubglotticSuctionSurgicalAirway;
import com.datafascia.emerge.harms.vae.SubglotticSuctionUse;
import com.datafascia.emerge.harms.vae.Ventilated;
import com.datafascia.emerge.harms.vae.VentilationModeImpl;
import com.datafascia.emerge.ucsf.DailySpontaneousBreathingTrial;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.SATDSI;
import com.datafascia.emerge.ucsf.TimestampedBoolean;
import com.datafascia.emerge.ucsf.TimestampedInteger;
import com.datafascia.emerge.ucsf.TimestampedMaybe;
import com.datafascia.emerge.ucsf.VAE;
import com.datafascia.emerge.ucsf.VentilationMode;
import com.datafascia.emerge.ucsf.codes.ventilation.DailySpontaneousBreathingTrialContraindicatedEnum;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Updates ventilator associated event data for a patient.
 */
public class VentilatorAssociatedEventUpdater {

  @Inject
  private Clock clock;

  @Inject
  private DailySedationInterruptionCandidate dailySedationInterruptionCandidate;

  @Inject
  private DailySedationInterruption dailySedationInterruption;

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
  private SubglotticSuctionSurgicalAirway subglotticSuctionSurgicalAirway;

  @Inject
  private SubglotticSuctionNonSurgicalAirway subglotticSuctionNonSurgicalAirway;

  @Inject
  private SubglotticSuctionUse subglotticSuctionUse;

  @Inject
  private OralCare oralCare;

  @Inject
  private InlineSuction inlineSuction;

  @Inject
  private DailySpontaneousBreathingTrialImpl dailySpontaneousBreathingTrial;

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
   * Updates daily sedation interruption.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateDailySedationInterruption(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    DailySedationInterruptionCandidate.CandidateResult candidateResult =
        dailySedationInterruptionCandidate.getDailySedationInterruptionCandidate(encounterId);
    boolean performed = dailySedationInterruption.test(encounterId);

    SATDSI dailySedationInterruption = new SATDSI()
        .withCandidate(
            candidateResult.isCandidate())
        .withNotCandidateReason(
            SATDSI.NotCandidateReason.fromValue(candidateResult.getNotCandidateReason()))
        .withPerformed(
            performed)
        .withUpdateTime(
            Date.from(Instant.now(clock)));

    harmEvidence.getMedicalData().setSATDSI(dailySedationInterruption);
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
   * Updates subglottic suction surgical airway.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateSubglotticSuctionSurgicalAirway(
      HarmEvidence harmEvidence, Encounter encounter) {

    String encounterId = encounter.getId().getIdPart();
    boolean value = subglotticSuctionSurgicalAirway.test(encounterId);

    TimestampedMaybe newSubglotticSuctionSurgicalAirway = new TimestampedMaybe()
        .withValue(value ? TimestampedMaybe.Value.YES : TimestampedMaybe.Value.NO)
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setSubglotticSuctionSurgicalAirway(
        newSubglotticSuctionSurgicalAirway);
  }

  /**
   * Updates subglottic suction non-surgical airway.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateSubglotticSuctionNonSurgicalAirway(
      HarmEvidence harmEvidence, Encounter encounter) {

    String encounterId = encounter.getId().getIdPart();

    TimestampedBoolean newSubglotticSuctionNonSurgicalAirway = new TimestampedBoolean()
        .withValue(subglotticSuctionNonSurgicalAirway.test(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setSubglotticSuctionNonSurgicalAirway(
        newSubglotticSuctionNonSurgicalAirway);
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

  /**
   * Updates oral care.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateOralCare(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    String value = oralCare.apply(encounterId).getCode();

    TimestampedMaybe newOralCare = new TimestampedMaybe()
        .withValue(TimestampedMaybe.Value.fromValue(value))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setOralCare(newOralCare);
  }

  /**
   * Updates inline suction.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateInlineSuction(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    TimestampedBoolean newInlineSuction = new TimestampedBoolean()
        .withValue(inlineSuction.test(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setInlineSuction(newInlineSuction);
  }

  /**
   * Updates daily spontaneous breathing trial.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateDailySpontaneousBreathingTrial(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();
    String value = dailySpontaneousBreathingTrial.getValue(encounterId).getCode();
    Optional<DailySpontaneousBreathingTrialContraindicatedEnum> optionalContraindicatedReason =
        dailySpontaneousBreathingTrial.getContraindicatedReason(encounterId);

    DailySpontaneousBreathingTrial newBreathingTrial = new DailySpontaneousBreathingTrial()
        .withValue(
            DailySpontaneousBreathingTrial.Value.fromValue(value))
        .withContraindicatedReason(
            optionalContraindicatedReason.map(reason ->
                DailySpontaneousBreathingTrial.ContraindicatedReason.fromValue(reason.getCode()))
            .orElse(null))
        .withUpdateTime(
            Date.from(Instant.now(clock)));

    getVAE(harmEvidence).setDailySpontaneousBreathingTrial(newBreathingTrial);
  }
}
