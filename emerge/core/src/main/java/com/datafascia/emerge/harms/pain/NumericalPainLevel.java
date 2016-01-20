// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.Periods;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Data;

/**
 * Implements the Pain and Delirium Numerical Pain Level
 */
public class NumericalPainLevel {
  private enum MinOrMax {
    MIN, MAX
  }

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  /**
   * Result container for the Pain and Delirium Numerical Pain Level (Current)
   */
  @Data @Builder
  public static class CurrentPainLevel {
    private int painScore;
    private Date timeOfDataAquisition;
    private Date effectiveDateTime;
  }

  /**
   * Checks if observation is relevant to Numerical Pain.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to Numerical Pain.
   */
  public static boolean isRelevant(Observation observation) {
    return (ObservationCodeEnum.NUMERICAL_PAIN_01.isCodeEquals(observation.getCode()) ||
            ObservationCodeEnum.NUMERICAL_PAIN_02.isCodeEquals(observation.getCode()) ||
            ObservationCodeEnum.NUMERICAL_PAIN_03.isCodeEquals(observation.getCode()) ||
            ObservationCodeEnum.NUMERICAL_PAIN_04.isCodeEquals(observation.getCode()));
  }

  /**
   * Result container for the Pain and Delirium Numerical Pain Level (Minimum or Maximum)
   */
  @Data @Builder
  public static class MinimumOrMaximumPainLevel {
    private int painScore;
    private Date timeOfCalculation;
    private Date startOfTimePeriod;
    private Date endOfTimePeriod;
  }

  /**
   * Implements the Pain and Delirium Numerical Pain Level (Current)
   * Returns the pain score and acquisition time from the newest observation with the highest score.
   *
   * @param encounterId
   *     encounter to check.
   * @return Numeric pain level and time of acquisition, or {@code null} if not found
   */
  public CurrentPainLevel getCurrentPainLevel(String encounterId) {
    PeriodDt sevenHoursAgo = Periods.getPastHoursToNow(clock, 7);

    CurrentPainLevel result = CurrentPainLevel.builder()
        .painScore(11)
        .timeOfDataAquisition(sevenHoursAgo.getStart())
        .build();

    List<Observation> observationsSinceSevenHoursAgo =
        ObservationUtils.searchByTimeFrame(apiClient, encounterId, sevenHoursAgo);

    Observation freshestNumericalPainScore = PainUtils.freshestHighestNumericalPainScore(
        observationsSinceSevenHoursAgo);

    if (freshestNumericalPainScore != null) {
      result.setEffectiveDateTime(ObservationUtils.getEffectiveDate(freshestNumericalPainScore));
      result.setPainScore(PainUtils.getPainScoreFromValue(freshestNumericalPainScore));
      result.setTimeOfDataAquisition(ObservationUtils.getEffectiveDate(freshestNumericalPainScore));
    }
    return result;
  }

  /**
   * Implements the Pain and Delirium Numerical Pain Level (Low)
   * Returns the lowest pain score from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param encounterId
   *     encounter to check.
   * @return Numeric pain level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumPainLevel getDailyMin(String encounterId) {
    return getDailyMinOrMax(encounterId, MinOrMax.MIN);
  }

  /**
   * Implements the Pain and Delirium Numerical Pain Level (High)
   * Returns the highest pain score from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param encounterId
   *     encounter to check.
   * @return Numeric pain level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumPainLevel getDailyMax(String encounterId) {
    return getDailyMinOrMax(encounterId, MinOrMax.MAX);
  }

  /**
   * Implements the Pain and Delirium Numerical Pain Level (High/Low)
   * Returns the pain score and acquisition time from the newest observation with
   * either the highest or lowest score, depending on MinOrMax.
   *
   * @param encounterId
   *     encounter to check.
   * @param minOrMax
   *     Whether to return the highest or lowest pain level.
   * @return Numeric pain level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  private MinimumOrMaximumPainLevel getDailyMinOrMax(String encounterId, MinOrMax minOrMax) {
    PeriodDt sinceMidnight = Periods.getMidnightToNow(clock);

    MinimumOrMaximumPainLevel result = MinimumOrMaximumPainLevel
        .builder()
        .painScore(11)
        .timeOfCalculation(sinceMidnight.getEnd())
        .startOfTimePeriod(sinceMidnight.getStart())
        .endOfTimePeriod(sinceMidnight.getEnd())
        .build();

    List<Observation> observationsSinceMidnight = ObservationUtils.searchByTimeFrame(apiClient,
        encounterId, sinceMidnight);

    Observation highestOrLowestNumericalPainScore = null;

    if (minOrMax == MinOrMax.MIN) {
      highestOrLowestNumericalPainScore = PainUtils.lowestNumericalPainScore(
          observationsSinceMidnight);
    } else if (minOrMax == MinOrMax.MAX) {
      highestOrLowestNumericalPainScore = PainUtils.highestNumericalPainScore(
          observationsSinceMidnight);
    }

    Integer highestOrLowestScoreValue = PainUtils.getPainScoreFromValue(
        highestOrLowestNumericalPainScore);

    if (highestOrLowestScoreValue != null) {
      result.setPainScore(highestOrLowestScoreValue);
      result.setTimeOfCalculation(sinceMidnight.getEnd());
      result.setStartOfTimePeriod(sinceMidnight.getStart());
      result.setEndOfTimePeriod(sinceMidnight.getEnd());
    }
    return result;
  }

}
