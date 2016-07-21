// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.common.inject.Injectors;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.Periods;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Data;

/**
 * Implements the Pain and Delirium Numerical Pain Level
 */
public class NumericalPainLevel {

  private static final int NUMERICAL_PAIN_LOOKBACK = 7;

  private static final Set<String> NUMERICAL_PAIN_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.NUMERICAL_PAIN_01.getCode(),
      ObservationCodeEnum.NUMERICAL_PAIN_02.getCode(),
      ObservationCodeEnum.NUMERICAL_PAIN_03.getCode(),
      ObservationCodeEnum.NUMERICAL_PAIN_04.getCode());

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  private enum MinOrMax {
    MIN, MAX
  }

  private Date nowDate = null;

  /**
   * Result container for all of the numerical pain levels (current + minimum + maximum)
   */
  @Data @Builder
  public static class AllNumericPainLevels {
    private CurrentPainLevel current;
    private MinimumOrMaximumPainLevel min;
    private MinimumOrMaximumPainLevel max;
  }

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
   * Checks if observation is relevant to Numerical Pain and within the necessary time window.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to Numerical Pain.
   */
  public static boolean isRelevant(Observation observation) {
    Clock clock = Injectors.getInjector().getInstance(Clock.class);
    PeriodDt currentPainTimeRange = Periods.getPastHoursToNow(clock, NUMERICAL_PAIN_LOOKBACK);
    PeriodDt painMinMaxTimeRange = Periods.getMidnightToNow(clock);

    return (ObservationUtils.isAfter(
        observation, currentPainTimeRange.getStart()) ||
          ObservationUtils.isAfter(
            observation, painMinMaxTimeRange.getStart())) &&
        (ObservationCodeEnum.NUMERICAL_PAIN_01.isCodeEquals(observation.getCode())
        || ObservationCodeEnum.NUMERICAL_PAIN_02.isCodeEquals(observation.getCode())
        || ObservationCodeEnum.NUMERICAL_PAIN_03.isCodeEquals(observation.getCode())
        || ObservationCodeEnum.NUMERICAL_PAIN_04.isCodeEquals(observation.getCode()));
  }

  /**
   * Wraps implementation for all numerical pain levels (current, minimum, maximum)
   *
   * @param encounterId
   *     encounter to check.
   * @return Numeric pain level and time of acquisition, or {@code null} if not found
   */
  public AllNumericPainLevels getAllNumericPainLevels(String encounterId) {
    PeriodDt currentPainTimeRange = Periods.getPastHoursToNow(clock, NUMERICAL_PAIN_LOOKBACK);
    PeriodDt painMinMaxTimeRange = Periods.getMidnightToNow(clock);
    Observations observations = apiClient.getObservationClient().list(encounterId);
    nowDate = painMinMaxTimeRange.getEnd();

    return getAllNumericPainLevels(observations, currentPainTimeRange, painMinMaxTimeRange);
  }

  /**
   * Wraps implementation for all numeric pain levels (current, minimum, maximum)
   * Handles all the observation filtering required.
   *
   * @param observations
   *     Observations for the encounter.
   * @param currentPainTimeRange
   *     Time range for the current pain level.
   * @param minMaxTimeRange
   *     Time range for the pain min and max.
   * @return Current, minimum and maximum pain levels.
   */
  public AllNumericPainLevels getAllNumericPainLevels(Observations observations,
      PeriodDt currentPainTimeRange,
      PeriodDt minMaxTimeRange) {
    Instant lowestTimeBound = null;
    if (currentPainTimeRange.getStart().after(minMaxTimeRange.getStart())) {
      lowestTimeBound = minMaxTimeRange.getStart().toInstant();
    } else {
      lowestTimeBound = currentPainTimeRange.getStart().toInstant();
    }

    List<Observation> recentPainObservations = observations.list(NUMERICAL_PAIN_OBSERVATION_CODES,
        lowestTimeBound, null);

    List<Observation> currentPainObservations = recentPainObservations
        .stream()
        .filter(obs -> ObservationUtils.insideTimeFrame(obs, currentPainTimeRange))
        .collect(Collectors.toList());
    CurrentPainLevel currentLevel = getCurrentPainLevel(currentPainObservations,
        currentPainTimeRange);

    List<Observation> minMaxObservations = recentPainObservations
        .stream()
        .filter(obs -> ObservationUtils.insideTimeFrame(obs, minMaxTimeRange))
        .collect(Collectors.toList());
    MinimumOrMaximumPainLevel maxLevel = getDailyMax(minMaxObservations, minMaxTimeRange);
    MinimumOrMaximumPainLevel minLevel = getDailyMin(minMaxObservations, minMaxTimeRange);

    return AllNumericPainLevels.builder()
        .current(currentLevel)
        .min(minLevel)
        .max(maxLevel)
        .build();
  }

  /**
   * Implements the Pain and Delirium Numerical Pain Level (Current)
   * Returns the pain score and acquisition time from the newest observation with the highest score.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return Numeric pain level and time of acquisition, or {@code null} if not found
   */
  public CurrentPainLevel getCurrentPainLevel(List<Observation> observations,
      PeriodDt timeRange) {
    CurrentPainLevel result = CurrentPainLevel.builder()
        .painScore(11)
        .timeOfDataAquisition(nowDate)
        .build();

    Observation freshestNumericalPainScore = PainUtils.freshestHighestNumericalPainScore(
        observations);

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
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return Numeric pain level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumPainLevel getDailyMin(List<Observation> observations,
      PeriodDt timeRange) {
    return getDailyMinOrMax(observations, timeRange, MinOrMax.MIN);
  }

  /**
   * Implements the Pain and Delirium Numerical Pain Level (High)
   * Returns the highest pain score from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return Numeric pain level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumPainLevel getDailyMax(List<Observation> observations,
      PeriodDt timeRange) {
    return getDailyMinOrMax(observations, timeRange, MinOrMax.MAX);
  }

  /**
   * Implements the Pain and Delirium Numerical Pain Level (High/Low)
   * Returns the pain score and acquisition time from the newest observation with
   * either the highest or lowest score, depending on MinOrMax.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @param minOrMax
   *     Whether to return the highest or lowest pain level.
   * @return Numeric pain level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  private MinimumOrMaximumPainLevel getDailyMinOrMax(List<Observation> observations,
      PeriodDt timeRange, MinOrMax minOrMax) {
    MinimumOrMaximumPainLevel result = MinimumOrMaximumPainLevel
        .builder()
        .painScore(11)
        .timeOfCalculation(timeRange.getEnd())
        .startOfTimePeriod(timeRange.getStart())
        .endOfTimePeriod(timeRange.getEnd())
        .build();

    Observation highestOrLowestNumericalPainScore = null;

    if (minOrMax == MinOrMax.MIN) {
      highestOrLowestNumericalPainScore = PainUtils.lowestNumericalPainScore(
          observations);
    } else if (minOrMax == MinOrMax.MAX) {
      highestOrLowestNumericalPainScore = PainUtils.highestNumericalPainScore(
          observations);
    }

    Integer highestOrLowestScoreValue = PainUtils.getPainScoreFromValue(
        highestOrLowestNumericalPainScore);

    if (highestOrLowestScoreValue != null) {
      result.setPainScore(highestOrLowestScoreValue);
      result.setTimeOfCalculation(timeRange.getEnd());
      result.setStartOfTimePeriod(timeRange.getStart());
      result.setEndOfTimePeriod(timeRange.getEnd());
    }
    return result;
  }
}
