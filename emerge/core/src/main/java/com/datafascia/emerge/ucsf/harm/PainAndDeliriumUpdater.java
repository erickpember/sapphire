// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.pain.BenzodiazepineAvoidance;
import com.datafascia.emerge.harms.pain.CamImpl;
import com.datafascia.emerge.harms.pain.CamImpl.CamImplResult;
import com.datafascia.emerge.harms.pain.CpotImpl;
import com.datafascia.emerge.harms.pain.CpotImpl.CurrentCpotLevel;
import com.datafascia.emerge.harms.pain.CpotImpl.MinimumOrMaximumCpotLevel;
import com.datafascia.emerge.harms.pain.NumericalPainLevel;
import com.datafascia.emerge.harms.pain.NumericalPainLevel.CurrentPainLevel;
import com.datafascia.emerge.harms.pain.NumericalPainLevel.MinimumOrMaximumPainLevel;
import com.datafascia.emerge.harms.pain.PainGoalImpl;
import com.datafascia.emerge.harms.pain.SedativeOrder;
import com.datafascia.emerge.harms.pain.SedativeOrder.OrderResult;
import com.datafascia.emerge.harms.pain.VerbalPainLevel;
import com.datafascia.emerge.harms.rass.RassGoalImpl;
import com.datafascia.emerge.harms.rass.RassGoalImpl.RassGoalResult;
import com.datafascia.emerge.harms.rass.RassLevel;
import com.datafascia.emerge.harms.rass.RassLevel.CurrentRassLevel;
import com.datafascia.emerge.harms.rass.RassLevel.MinimumOrMaximumRassLevel;
import com.datafascia.emerge.ucsf.Cam;
import com.datafascia.emerge.ucsf.Cpot;
import com.datafascia.emerge.ucsf.CurrentScore;
import com.datafascia.emerge.ucsf.CurrentScore_;
import com.datafascia.emerge.ucsf.CurrentScore__;
import com.datafascia.emerge.ucsf.CurrentScore___;
import com.datafascia.emerge.ucsf.DailyMax;
import com.datafascia.emerge.ucsf.DailyMax_;
import com.datafascia.emerge.ucsf.DailyMax__;
import com.datafascia.emerge.ucsf.DailyMax___;
import com.datafascia.emerge.ucsf.DailyMin;
import com.datafascia.emerge.ucsf.DailyMin_;
import com.datafascia.emerge.ucsf.DailyMin__;
import com.datafascia.emerge.ucsf.DailyMin___;
import com.datafascia.emerge.ucsf.Delirium;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.Numerical;
import com.datafascia.emerge.ucsf.Order;
import com.datafascia.emerge.ucsf.Pain;
import com.datafascia.emerge.ucsf.PainGoal;
import com.datafascia.emerge.ucsf.RASS;
import com.datafascia.emerge.ucsf.RassGoal;
import com.datafascia.emerge.ucsf.Sedative;
import com.datafascia.emerge.ucsf.TimestampedBoolean;
import com.datafascia.emerge.ucsf.Verbal;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;

/**
 * Updates pain and delirium associated data for a patient.
 */
public class PainAndDeliriumUpdater {

  @Inject
  private Clock clock;

  @Inject
  private PainGoalImpl painGoalImpl;

  @Inject
  private NumericalPainLevel numericalPainLevelImpl;

  @Inject
  private VerbalPainLevel verbalPainLevelImpl;

  @Inject
  private RassLevel rassLevelImpl;

  @Inject
  private RassGoalImpl rassGoalImpl;

  @Inject
  private CpotImpl cpotImpl;

  @Inject
  private CamImpl camImpl;

  @Inject
  private BenzodiazepineAvoidance benzodiazepineAvoidanceImpl;

  @Inject
  private SedativeOrder sedativeOrderImpl;

  private static Delirium getDelirium(HarmEvidence harmEvidence) {
    MedicalData medicalData = harmEvidence.getMedicalData();

    Delirium delirium = medicalData.getDelirium();
    if (delirium == null) {
      delirium = new Delirium();
      medicalData.setDelirium(delirium);
    }
    return delirium;
  }

  private static Pain getPain(HarmEvidence harmEvidence) {
    Delirium delirium = getDelirium(harmEvidence);

    Pain pain = delirium.getPain();
    if (pain == null) {
      pain = new Pain();
      delirium.setPain(pain);
    }
    return pain;
  }

  /**
   * Updates numerical pain level.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateNumericalPainLevel(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    PainGoal painGoal = new PainGoal().withDataEntryTime(Date.from(Instant.now(clock)))
        .withGoal(painGoalImpl.getPainGoal(encounterId));
    getPain(harmEvidence).setPainGoal(painGoal);

    CurrentPainLevel currentLevel = numericalPainLevelImpl.getCurrentPainLevel(encounterId);
    CurrentScore currentScore = new CurrentScore()
        .withPainScore(currentLevel.getPainScore())
        .withTimeOfDataAquisition(currentLevel.getTimeOfDataAquisition());

    MinimumOrMaximumPainLevel maxLevel = numericalPainLevelImpl.getDailyMax(encounterId);
    DailyMax dailyMax = new DailyMax()
        .withEndOfTimePeriod(maxLevel.getEndOfTimePeriod())
        .withPainMax(maxLevel.getPainScore())
        .withStartOfTimePeriod(maxLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(maxLevel.getTimeOfCalculation());

    MinimumOrMaximumPainLevel minLevel = numericalPainLevelImpl.getDailyMin(encounterId);
    DailyMin dailyMin = new DailyMin()
        .withEndOfTimePeriod(minLevel.getEndOfTimePeriod())
        .withPainMin(minLevel.getPainScore())
        .withStartOfTimePeriod(minLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(minLevel.getTimeOfCalculation());

    Numerical numerical = new Numerical()
        .withDailyMin(dailyMin)
        .withDailyMax(dailyMax)
        .withCurrentScore(currentScore);

    getPain(harmEvidence).setNumerical(numerical);
  }

  /**
   * Updates verbal pain level.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateVerbalPainLevel(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    VerbalPainLevel.CurrentPainLevel currentLevel = verbalPainLevelImpl.getCurrentPainLevel(
        encounterId);
    CurrentScore_ currentScore = new CurrentScore_()
        .withPainScore(currentLevel.getPainScore())
        .withTimeOfDataAquisition(currentLevel.getTimeOfDataAquisition());

    VerbalPainLevel.MinimumOrMaximumPainLevel maxLevel = verbalPainLevelImpl
        .getDailyMax(encounterId);
    DailyMax_ dailyMax = new DailyMax_()
        .withEndOfTimePeriod(maxLevel.getEndOfTimePeriod())
        .withPainMax(maxLevel.getPainScore())
        .withStartOfTimePeriod(maxLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(maxLevel.getTimeOfCalculation());

    VerbalPainLevel.MinimumOrMaximumPainLevel minLevel = verbalPainLevelImpl
        .getDailyMin(encounterId);
    DailyMin_ dailyMin = new DailyMin_()
        .withEndOfTimePeriod(minLevel.getEndOfTimePeriod())
        .withPainMin(minLevel.getPainScore())
        .withStartOfTimePeriod(minLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(minLevel.getTimeOfCalculation());

    Verbal verbal = new Verbal()
        .withDailyMin(dailyMin)
        .withDailyMax(dailyMax)
        .withCurrentScore(currentScore);

    getPain(harmEvidence).setVerbal(verbal);
  }

  /**
   * Updates CPOT level.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateCpotLevel(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    CurrentCpotLevel currentLevel = cpotImpl.getCurrentCpotLevel(encounterId);
    CurrentScore__ currentScore = new CurrentScore__()
        .withPainScore(currentLevel.getPainScore())
        .withTimeOfDataAquisition(currentLevel.getTimeOfDataAquisition());

    MinimumOrMaximumCpotLevel maxLevel = cpotImpl.getCpotMax(encounterId);
    DailyMax__ dailyMax = new DailyMax__()
        .withEndOfTimePeriod(maxLevel.getEndOfTimePeriod())
        .withPainMax(maxLevel.getPainScore())
        .withStartOfTimePeriod(maxLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(maxLevel.getTimeOfCalculation());

    MinimumOrMaximumCpotLevel minLevel = cpotImpl.getCpotMin(encounterId);
    DailyMin__ dailyMin = new DailyMin__()
        .withEndOfTimePeriod(minLevel.getEndOfTimePeriod())
        .withPainMin(minLevel.getPainScore())
        .withStartOfTimePeriod(minLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(minLevel.getTimeOfCalculation());

    Cpot cpot = new Cpot()
        .withDailyMin(dailyMin)
        .withDailyMax(dailyMax)
        .withCurrentScore(currentScore);

    getPain(harmEvidence).setCpot(cpot);
  }

  /**
   * Updates RASS.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateRass(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    RassGoalResult rassGoalResult = rassGoalImpl.getRassGoal(encounterId);
    RassGoal rassGoal = new RassGoal()
        .withGoal(rassGoalResult.getGoal())
        .withDataEntryTime(rassGoalResult.getDataEntryTime());

    CurrentRassLevel currentLevel = rassLevelImpl.getCurrentRassLevel(encounterId);
    CurrentScore___ currentScore = new CurrentScore___()
        .withRASSScore(currentLevel.getRassScore())
        .withTimeOfDataAquisition(currentLevel.getTimeOfDataAquisition());

    MinimumOrMaximumRassLevel maxLevel = rassLevelImpl.getRassMax(encounterId);
    DailyMax___ dailyMax = new DailyMax___()
        .withEndOfTimePeriod(maxLevel.getEndOfTimePeriod())
        .withRASSMax(maxLevel.getRassScore())
        .withStartOfTimePeriod(maxLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(maxLevel.getTimeOfCalculation());

    MinimumOrMaximumRassLevel minLevel = rassLevelImpl.getRassMin(encounterId);
    DailyMin___ dailyMin = new DailyMin___()
        .withEndOfTimePeriod(minLevel.getEndOfTimePeriod())
        .withRASSMin(minLevel.getRassScore())
        .withStartOfTimePeriod(minLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(minLevel.getTimeOfCalculation());

    RASS rass = new RASS()
        .withRassGoal(rassGoal)
        .withDailyMin(dailyMin)
        .withDailyMax(dailyMax)
        .withCurrentScore(currentScore);

    getDelirium(harmEvidence).setRASS(rass);
  }

  /**
   * Updates Cam.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateCam(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    CamImplResult camImplResult = camImpl.getCam(encounterId);
    String camResult = camImplResult.getResult();
    String camUtaReason = camImplResult.getUtaReason();

    Cam cam = new Cam()
        .withResult(Cam.Result.fromValue(camResult))
        .withUpdateTime(Date.from(Instant.now(clock)))
        .withUtaReason((camUtaReason != null) ? Cam.UtaReason.fromValue(camUtaReason) : null);

    getDelirium(harmEvidence).setCam(cam);
  }

  /**
   * Updates Sedative.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateSedative(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    List<Order> orders = new ArrayList<>();
    Set<String> BENZODIAZEPINE_NAMES = ImmutableSet.of(
        MedsSetEnum.INTERMITTENT_LORAZEPAM_IV.getCode(),
        MedsSetEnum.INTERMITTENT_LORAZEPAM_ENTERAL.getCode(),
        MedsSetEnum.CONTINUOUS_INFUSION_LORAZEPAM_IV.getCode(),
        MedsSetEnum.INTERMITTENT_MIDAZOLAM_IV.getCode(),
        MedsSetEnum.CONTINUOUS_INFUSION_MIDAZOLAM_IV.getCode(),
        MedsSetEnum.INTERMITTENT_CLONAZEPAM_ENTERAL.getCode(),
        MedsSetEnum.INTERMITTENT_DIAZEPAM_IV.getCode(),
        MedsSetEnum.INTERMITTENT_DIAZEPAM_ENTERAL.getCode(),
        MedsSetEnum.INTERMITTENT_CHLORADIAZEPOXIDE_ENTERAL.getCode(),
        MedsSetEnum.INTERMITTENT_ALPRAZALOM_ENTERAL.getCode());

    for (String medsSet : BENZODIAZEPINE_NAMES) {
      Optional<OrderResult> result = sedativeOrderImpl.getValue(encounterId, medsSet);
      if (result.isPresent()) {
        Order order = new Order()
            .withDosageRoute((result.get().getDosageRoute() != null) ? Order.DosageRoute.fromValue(
                        result.get().getDosageRoute()) : null)
            .withDrug((result.get().getDrug() != null) ? Order.Drug
                    .fromValue(result.get().getDrug()) : null)
            .withUpdateTime(Date.from(Instant.now(clock)));
        orders.add(order);
      }
    }

    TimestampedBoolean benzodiazepineContraindicated = new TimestampedBoolean()
        .withValue(benzodiazepineAvoidanceImpl
            .isBenzodiazepineAvoidanceContraindicated(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));

    Sedative sedative = new Sedative().withContraindicated(benzodiazepineContraindicated)
        .withOrders(orders);

    getDelirium(harmEvidence).setSedative(sedative);
  }
}
