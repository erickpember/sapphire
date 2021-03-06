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
package com.datafascia.emerge.ucsf.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.pain.BenzodiazepineAvoidance;
import com.datafascia.emerge.harms.pain.CamImpl;
import com.datafascia.emerge.harms.pain.CamImpl.CamImplResult;
import com.datafascia.emerge.harms.pain.CpotImpl;
import com.datafascia.emerge.harms.pain.CpotImpl.AllCpotLevels;
import com.datafascia.emerge.harms.pain.CpotImpl.CurrentCpotLevel;
import com.datafascia.emerge.harms.pain.CpotImpl.MinimumOrMaximumCpotLevel;
import com.datafascia.emerge.harms.pain.NumericalPainLevel;
import com.datafascia.emerge.harms.pain.NumericalPainLevel.AllNumericPainLevels;
import com.datafascia.emerge.harms.pain.NumericalPainLevel.CurrentPainLevel;
import com.datafascia.emerge.harms.pain.NumericalPainLevel.MinimumOrMaximumPainLevel;
import com.datafascia.emerge.harms.pain.PainGoalImpl;
import com.datafascia.emerge.harms.pain.SedativeOrder;
import com.datafascia.emerge.harms.pain.VerbalPainLevel;
import com.datafascia.emerge.harms.pain.VerbalPainLevel.AllVerbalPainLevels;
import com.datafascia.emerge.harms.rass.RassGoalImpl;
import com.datafascia.emerge.harms.rass.RassGoalImpl.RassGoalResult;
import com.datafascia.emerge.harms.rass.RassLevel;
import com.datafascia.emerge.harms.rass.RassLevel.AllRassLevels;
import com.datafascia.emerge.harms.rass.RassLevel.CurrentRassLevel;
import com.datafascia.emerge.harms.rass.RassLevel.MinimumOrMaximumRassLevel;
import com.datafascia.emerge.harms.vae.DailySedationInterruptionCandidate;
import com.datafascia.emerge.harms.vae.DailySedationInterruptionCandidate.CandidateResult;
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
import com.datafascia.emerge.ucsf.Nmba;
import com.datafascia.emerge.ucsf.Numerical;
import com.datafascia.emerge.ucsf.Order;
import com.datafascia.emerge.ucsf.Pain;
import com.datafascia.emerge.ucsf.PainGoal;
import com.datafascia.emerge.ucsf.RASS;
import com.datafascia.emerge.ucsf.RassGoal;
import com.datafascia.emerge.ucsf.Sedative;
import com.datafascia.emerge.ucsf.TimestampedBoolean;
import com.datafascia.emerge.ucsf.Verbal;
import java.time.Clock;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
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
  private DailySedationInterruptionCandidate dailySedationInterruptionCandidate;

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

    PainGoal painGoal = new PainGoal().withGoal(painGoalImpl.getPainGoal(encounterId));
    getPain(harmEvidence).setPainGoal(painGoal);

    AllNumericPainLevels painLevels = numericalPainLevelImpl.getAllNumericPainLevels(encounterId);

    CurrentPainLevel currentLevel = painLevels.getCurrent();
    painGoal.setDataEntryTime(currentLevel.getEffectiveDateTime());
    CurrentScore currentScore = new CurrentScore()
        .withPainScore(currentLevel.getPainScore())
        .withTimeOfDataAquisition(currentLevel.getTimeOfDataAquisition());

    MinimumOrMaximumPainLevel maxLevel = painLevels.getMax();
    DailyMax dailyMax = new DailyMax()
        .withEndOfTimePeriod(maxLevel.getEndOfTimePeriod())
        .withPainMax(maxLevel.getPainScore())
        .withStartOfTimePeriod(maxLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(maxLevel.getTimeOfCalculation());

    MinimumOrMaximumPainLevel minLevel = painLevels.getMin();
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

    PainGoal painGoal = new PainGoal().withDataEntryTime(Date.from(Instant.now(clock)))
        .withGoal(painGoalImpl.getPainGoal(encounterId));
    getPain(harmEvidence).setPainGoal(painGoal);

    AllVerbalPainLevels painLevels = verbalPainLevelImpl.getAllVerbalPainLevels(encounterId);
    VerbalPainLevel.CurrentPainLevel currentLevel = painLevels.getCurrent();

    painGoal.setDataEntryTime(currentLevel.getTimeOfDataAquisition());
    CurrentScore_ currentScore = new CurrentScore_()
        .withPainScore(currentLevel.getPainScore())
        .withTimeOfDataAquisition(currentLevel.getTimeOfDataAquisition());

    VerbalPainLevel.MinimumOrMaximumPainLevel maxLevel = painLevels.getMax();
    DailyMax_ dailyMax = new DailyMax_()
        .withEndOfTimePeriod(maxLevel.getEndOfTimePeriod())
        .withPainMax(maxLevel.getPainScore())
        .withStartOfTimePeriod(maxLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(maxLevel.getTimeOfCalculation());

    VerbalPainLevel.MinimumOrMaximumPainLevel minLevel = painLevels.getMin();
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

    PainGoal painGoal = new PainGoal().withDataEntryTime(Date.from(Instant.now(clock)))
        .withGoal(painGoalImpl.getPainGoal(encounterId));
    getPain(harmEvidence).setPainGoal(painGoal);

    AllCpotLevels cpotLevels = cpotImpl.getAllCpotLevels(encounterId);

    CurrentCpotLevel currentLevel = cpotLevels.getCurrent();
    CurrentScore__ currentScore = new CurrentScore__()
        .withPainScore(currentLevel.getPainScore())
        .withTimeOfDataAquisition(currentLevel.getTimeOfDataAquisition());

    MinimumOrMaximumCpotLevel maxLevel = cpotLevels.getMax();
    DailyMax__ dailyMax = new DailyMax__()
        .withEndOfTimePeriod(maxLevel.getEndOfTimePeriod())
        .withPainMax(maxLevel.getPainScore())
        .withStartOfTimePeriod(maxLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(maxLevel.getTimeOfCalculation());

    MinimumOrMaximumCpotLevel minLevel = cpotLevels.getMin();
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
   * Updates NMBA.
   *
   * @param harmEvidence
   *     to modify
   * @param encounter
   *     encounter
   */
  public void updateNmba(HarmEvidence harmEvidence, Encounter encounter) {
    String encounterId = encounter.getId().getIdPart();

    CandidateResult candidateResult =
        dailySedationInterruptionCandidate.getDailySedationInterruptionCandidate(encounterId);
    boolean status = candidateResult == CandidateResult.RECEIVING_NMBA;

    Nmba nmba = new Nmba()
        .withStatus(status)
        .withDataEntryTime(Date.from(Instant.now(clock)));

    getPain(harmEvidence).setNmba(nmba);
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

    AllRassLevels rassLevels = rassLevelImpl.getAllRassLevels(encounterId);

    CurrentRassLevel currentLevel = rassLevels.getCurrent();
    CurrentScore___ currentScore = new CurrentScore___()
        .withRASSScore(currentLevel.getRassScore())
        .withTimeOfDataAquisition(currentLevel.getTimeOfDataAquisition());

    MinimumOrMaximumRassLevel maxLevel = rassLevels.getMax();
    DailyMax___ dailyMax = new DailyMax___()
        .withEndOfTimePeriod(maxLevel.getEndOfTimePeriod())
        .withRASSMax(maxLevel.getRassScore())
        .withStartOfTimePeriod(maxLevel.getStartOfTimePeriod())
        .withTimeOfCalculation(maxLevel.getTimeOfCalculation());

    MinimumOrMaximumRassLevel minLevel = rassLevels.getMin();
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

    TimestampedBoolean benzodiazepineContraindicated = new TimestampedBoolean()
        .withValue(benzodiazepineAvoidanceImpl
            .isBenzodiazepineAvoidanceContraindicated(encounterId))
        .withUpdateTime(Date.from(Instant.now(clock)));

    List<Order> orders = new ArrayList<>();
    sedativeOrderImpl.getAllSedativeOrders(encounterId)
        .stream().forEach(result -> {
          Order order = new Order()
              .withDosageRoute(
              (result.getDosageRoute() != null)
                  ? Order.DosageRoute.fromValue(result.getDosageRoute()) : null)
              .withDrug(
              (result.getDrug() != null)
                  ? Order.Drug.fromValue(result.getDrug()) : null)
              .withStatus(
              (result.getStatus() != null)
                  ? Order.Status.fromValue(result.getStatus()) : null)
              .withOrderId(result.getOrderId())
              .withUpdateTime(
              Date.from(Instant.now(clock)));
          orders.add(order);
        });

    Sedative sedative = new Sedative()
        .withContraindicated(benzodiazepineContraindicated)
        .withOrders(orders);

    getDelirium(harmEvidence).setSedative(sedative);
  }
}
