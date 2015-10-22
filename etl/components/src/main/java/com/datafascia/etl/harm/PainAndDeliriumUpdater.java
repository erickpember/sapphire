// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.harm;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import com.datafascia.emerge.harms.pain.NumericalPainLevel;
import com.datafascia.emerge.harms.pain.NumericalPainLevel.CurrentPainLevel;
import com.datafascia.emerge.harms.pain.NumericalPainLevel.MinimumOrMaximumPainLevel;
import com.datafascia.emerge.ucsf.CurrentScore;
import com.datafascia.emerge.ucsf.DailyMax;
import com.datafascia.emerge.ucsf.DailyMin;
import com.datafascia.emerge.ucsf.Delirium;
import com.datafascia.emerge.ucsf.HarmEvidence;
import com.datafascia.emerge.ucsf.MedicalData;
import com.datafascia.emerge.ucsf.Numerical;
import com.datafascia.emerge.ucsf.Pain;
import javax.inject.Inject;

/**
 * Updates pain and delirium associated data for a patient.
 */
public class PainAndDeliriumUpdater {

  @Inject
  private NumericalPainLevel numericalPainLevelImpl;

  private static Pain getPain(HarmEvidence harmEvidence) {
    MedicalData medicalData = harmEvidence.getMedicalData();
    if (medicalData.getDelirium() == null) {
      medicalData.setDelirium(new Delirium());
    }

    Pain pain = medicalData.getDelirium().getPain();
    if (pain == null) {
      pain = new Pain();
      medicalData.getDelirium().setPain(pain);
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

}
