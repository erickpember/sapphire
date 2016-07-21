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
import com.google.common.base.Strings;
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
import lombok.extern.slf4j.Slf4j;

/**
 * Implements the Pain and Delirium Verbal Pain Level
 */
@Slf4j
public class VerbalPainLevel {
  private static final int VERBAL_PAIN_LOOKBACK = 7;

  private static final Set<String> VERBAL_PAIN_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.VERBAL_PAIN_01.getCode(),
      ObservationCodeEnum.VERBAL_PAIN_02.getCode(),
      ObservationCodeEnum.VERBAL_PAIN_03.getCode(),
      ObservationCodeEnum.VERBAL_PAIN_04.getCode());

  private enum MinOrMax {
    MIN, MAX
  }

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  private Date nowDate = null;

  /**
   * Result container for all of the verbal pain levels (current + minimum + maximum)
   */
  @Data @Builder
  public static class AllVerbalPainLevels {
    private CurrentPainLevel current;
    private MinimumOrMaximumPainLevel min;
    private MinimumOrMaximumPainLevel max;
  }

  /**
   * Checks if observation is relevant to Verbal Pain and within the necessary time window.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to Verbal Pain.
   */
  public static boolean isRelevant(Observation observation) {
    Clock clock = Injectors.getInjector().getInstance(Clock.class);
    PeriodDt currentPainTimeRange = Periods.getPastHoursToNow(clock, VERBAL_PAIN_LOOKBACK);
    PeriodDt painMinMaxTimeRange = Periods.getMidnightToNow(clock);

    return (ObservationUtils.isAfter(
        observation, currentPainTimeRange.getStart()) ||
        ObservationUtils.isAfter(
            observation, painMinMaxTimeRange.getStart())) &&
        (ObservationCodeEnum.VERBAL_PAIN_01.isCodeEquals(observation.getCode()) ||
        ObservationCodeEnum.VERBAL_PAIN_02.isCodeEquals(observation.getCode()) ||
        ObservationCodeEnum.VERBAL_PAIN_03.isCodeEquals(observation.getCode()) ||
        ObservationCodeEnum.VERBAL_PAIN_04.isCodeEquals(observation.getCode()));
  }

  /**
   * Wraps implementation for all verbal pain levels (current, minimum, maximum)
   *
   * @param encounterId
   *     encounter to check.
   * @return Verbal pain level and time of acquisition, or {@code null} if not found
   */
  public AllVerbalPainLevels getAllVerbalPainLevels(String encounterId) {
    PeriodDt currentPainTimeRange = Periods.getPastHoursToNow(clock, VERBAL_PAIN_LOOKBACK);
    PeriodDt painMinMaxTimeRange = Periods.getMidnightToNow(clock);
    Observations observations = apiClient.getObservationClient().list(encounterId);
    nowDate = painMinMaxTimeRange.getEnd();

    return getAllVerbalPainLevels(observations, currentPainTimeRange, painMinMaxTimeRange);
  }

  /**
   * Wraps implementation for all verbal pain levels (current, minimum, maximum)
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
  public AllVerbalPainLevels getAllVerbalPainLevels(Observations observations,
      PeriodDt currentPainTimeRange,
      PeriodDt minMaxTimeRange) {
    Instant lowestTimeBound = null;
    if (currentPainTimeRange.getStart().after(minMaxTimeRange.getStart())) {
      lowestTimeBound = minMaxTimeRange.getStart().toInstant();
    } else {
      lowestTimeBound = currentPainTimeRange.getStart().toInstant();
    }

    List<Observation> recentPainObservations = observations.list(VERBAL_PAIN_OBSERVATION_CODES,
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

    return AllVerbalPainLevels.builder()
        .current(currentLevel)
        .min(minLevel)
        .max(maxLevel)
        .build();
  }

  /**
   * Result container for the Pain and Delirium Verbal Pain Level (Current)
   */
  @Data @Builder
  public static class CurrentPainLevel {
    private int painScore;
    private Date timeOfDataAquisition;
  }

  /**
   * Result container for the Pain and Delirium Verbal Pain Level (Minimum or Maximum)
   */
  @Data @Builder
  public static class MinimumOrMaximumPainLevel {
    private int painScore;
    private Date timeOfCalculation;
    private Date startOfTimePeriod;
    private Date endOfTimePeriod;
  }

  /**
   * Implements the Pain and Delirium Verbal Pain Level (Current)
   * Returns the pain score and acquisition time from the newest observation with the highest score.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return Verbal pain level and time of acquisition, or {@code null} if not found
   */
  public CurrentPainLevel getCurrentPainLevel(List<Observation> observations, PeriodDt timeRange) {
    CurrentPainLevel result = CurrentPainLevel
        .builder()
        .painScore(11)
        .timeOfDataAquisition(nowDate)
        .build();

    Observation freshestVerbalPainScore = PainUtils.freshestHighestVerbalPainScore(observations);

    if (freshestVerbalPainScore != null && !Strings.isNullOrEmpty(
        ObservationUtils.getValueAsString(freshestVerbalPainScore))) {
      Integer painScore = PainUtils.getVerbalPainScoreFromValue(freshestVerbalPainScore);
      result.setPainScore((painScore != null) ? painScore : 11);
      result.setTimeOfDataAquisition(ObservationUtils.getEffectiveDate(freshestVerbalPainScore));
    } else {
      log.info("No freshest pain level available.");
    }
    return result;
  }

  /**
   * Implements the Pain and Delirium Verbal Pain Level (Low)
   * Returns the lowest pain score from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return Verbal pain level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumPainLevel getDailyMin(List<Observation> observations, PeriodDt timeRange) {
    return getDailyMinOrMax(observations, timeRange, MinOrMax.MIN);
  }

  /**
   * Implements the Pain and Delirium Verbal Pain Level (High)
   * Returns the highest pain score from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return Verbal pain level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumPainLevel getDailyMax(List<Observation> observations, PeriodDt timeRange) {
    return getDailyMinOrMax(observations, timeRange, MinOrMax.MAX);
  }

  /**
   * Implements the Pain and Delirium Verbal Pain Level (High/Low)
   * Returns the pain score and acquisition time from the newest observation with
   * either the highest or lowest score, depending on MinOrMax.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @param minOrMax
   *     Whether to return the highest or lowest pain level.
   * @return Verbal pain level, along with the period between midnight and now,
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

    Observation highestOrLowestVerbalPainScore = null;

    if (minOrMax == MinOrMax.MIN) {
      highestOrLowestVerbalPainScore = PainUtils.lowestVerbalPainScore(observations);
    } else if (minOrMax == MinOrMax.MAX) {
      highestOrLowestVerbalPainScore = PainUtils.highestVerbalPainScore(observations);
    }

    if (highestOrLowestVerbalPainScore != null) {
      result.setPainScore(PainUtils.getVerbalPainScoreFromValue(highestOrLowestVerbalPainScore));
      result.setTimeOfCalculation(timeRange.getEnd());
      result.setStartOfTimePeriod(timeRange.getStart());
      result.setEndOfTimePeriod(timeRange.getEnd());
    }
    return result;
  }

}
