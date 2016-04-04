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
import java.time.Clock;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Data;

/**
 * Implement the pain and delirium CPOT level
 */
public class CpotImpl {
  private enum MinOrMax {
    MIN, MAX
  }

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  private static final int CPOT_LOOKBACK = 7;

  /**
   * Result container for all of the CPOT levels. (current, minimum, maximum)
   */
  @Data @Builder
  public static class AllCpotLevels {
    private CurrentCpotLevel current;
    private MinimumOrMaximumCpotLevel min;
    private MinimumOrMaximumCpotLevel max;
  }

  /**
   * Result container for the pain and delirium CPOT level (current)
   */
  @Data @Builder
  public static class CurrentCpotLevel {
    private int painScore;
    private Date timeOfDataAquisition;
  }

  /**
   * Result container for the pain and delirium CPOT level (Minimum or Maximum)
   */
  @Data @Builder
  public static class MinimumOrMaximumCpotLevel {
    private int painScore;
    private Date timeOfCalculation;
    private Date startOfTimePeriod;
    private Date endOfTimePeriod;
  }

  /**
   * Checks if observation is relevant to CPOT and within the necessary time window.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to CPOT.
   */
  public static boolean isRelevant(Observation observation) {
    Clock clock = Injectors.getInjector().getInstance(Clock.class);
    PeriodDt currentCpotTimeRange = Periods.getPastHoursToNow(clock, CPOT_LOOKBACK);
    PeriodDt cpotMinMaxTimeRange = Periods.getMidnightToNow(clock);
    return (ObservationUtils.isAfter(
        observation, currentCpotTimeRange.getStart()) ||
          ObservationUtils.isAfter(
            observation, cpotMinMaxTimeRange.getStart())) &&
        ObservationCodeEnum.CPOT.isCodeEquals(observation.getCode());
  }

  /**
   * Wraps implementation for all CPOT levels (current, minimum, maximum)
   *
   * @param encounterId
   *     encounter to check.
   * @return CPOT levels and time of acquisition, or {@code null} if not found
   */
  public AllCpotLevels getAllCpotLevels(String encounterId) {
    PeriodDt currentCpotTimeRange = Periods.getPastHoursToNow(clock, CPOT_LOOKBACK);
    PeriodDt cpotMinMaxTimeRange = Periods.getMidnightToNow(clock);
    Observations observations = apiClient.getObservationClient().list(encounterId);

    return getAllCpotLevels(observations, currentCpotTimeRange, cpotMinMaxTimeRange);
  }

  /**
   * Wraps implementation for all numeric CPOT levels (current, minimum, maximum)
   * Handles all the observation filtering required.
   *
   * @param observations
   *     Observations for the encounter.
   * @param currentCpotTimeRange
   *     Time range for the current CPOT level.
   * @param minMaxTimeRange
   *     Time range for the CPOT min and max.
   * @return Current, minimum and maximum CPOT levels.
   */
  public AllCpotLevels getAllCpotLevels(Observations observations,
      PeriodDt currentCpotTimeRange,
      PeriodDt minMaxTimeRange) {
    Instant lowestTimeBound = null;
    if (currentCpotTimeRange.getStart().after(minMaxTimeRange.getStart())) {
      lowestTimeBound = minMaxTimeRange.getStart().toInstant();
    } else {
      lowestTimeBound = currentCpotTimeRange.getStart().toInstant();
    }

    List<Observation> recentCpotObservations = observations.list(ObservationCodeEnum.CPOT.getCode(),
        lowestTimeBound, null);

    List<Observation> currentCpotObservations = recentCpotObservations
        .stream()
        .filter(obs -> ObservationUtils.insideTimeFrame(obs, currentCpotTimeRange))
        .collect(Collectors.toList());
    CurrentCpotLevel currentLevel = getCurrentCpotLevel(currentCpotObservations,
        currentCpotTimeRange);

    List<Observation> minMaxObservations = recentCpotObservations
        .stream()
        .filter(obs -> ObservationUtils.insideTimeFrame(obs, minMaxTimeRange))
        .collect(Collectors.toList());
    MinimumOrMaximumCpotLevel maxLevel = getCpotMax(minMaxObservations, minMaxTimeRange);
    MinimumOrMaximumCpotLevel minLevel = getCpotMin(minMaxObservations, minMaxTimeRange);

    return AllCpotLevels.builder()
        .current(currentLevel)
        .min(minLevel)
        .max(maxLevel)
        .build();
  }

  /**
   * Implements the pain and delirium CPOT level (current)
   * Returns the CPOT level and acquisition time from the newest observation with the highest score.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return CPOT level and time of acquisition, or {@code null} if not found
   */
  public CurrentCpotLevel getCurrentCpotLevel(List<Observation> observations,
      PeriodDt timeRange) {
    CurrentCpotLevel result = CurrentCpotLevel
        .builder()
        .painScore(11)
        .timeOfDataAquisition(timeRange.getStart())
        .build();

    Observation freshestCpotScore = ObservationUtils.findFreshestObservation(observations);

    if (freshestCpotScore != null) {
      result.setPainScore(PainUtils.getPainScoreFromValue(freshestCpotScore));
      result.setTimeOfDataAquisition(ObservationUtils.getEffectiveDate(freshestCpotScore));
    }
    return result;
  }

  /**
   * Implements the pain and delirium CPOT level (low)
   * Returns the lowest CPOT level from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return CPOT level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumCpotLevel getCpotMin(List<Observation> observations,
      PeriodDt timeRange) {
    return getDailyMinOrMax(observations, timeRange, MinOrMax.MIN);
  }

  /**
   * Implements the pain and delirium CPOT level (high)
   * Returns the highest CPOT level from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return CPOT level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumCpotLevel getCpotMax(List<Observation> observations,
      PeriodDt timeRange) {
    return getDailyMinOrMax(observations, timeRange, MinOrMax.MAX);
  }

  /**
   * Implements the pain and delirium CPOT level (high/low)
   * Returns the CPOT level and acquisition time from the newest observation with
   * either the highest or lowest level, depending on MinOrMax.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @param minOrMax
   *     Whether to return the highest or lowest CPOT level.
   * @return CPOT level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  private MinimumOrMaximumCpotLevel getDailyMinOrMax(List<Observation> observations,
      PeriodDt timeRange, MinOrMax minOrMax) {
    MinimumOrMaximumCpotLevel result = MinimumOrMaximumCpotLevel
        .builder()
        .painScore(11)
        .timeOfCalculation(timeRange.getEnd())
        .startOfTimePeriod(timeRange.getStart())
        .endOfTimePeriod(timeRange.getEnd())
        .build();

    Observation highestOrLowestCpotLevel = null;

    if (minOrMax == MinOrMax.MIN) {
      highestOrLowestCpotLevel = PainUtils.lowestCpotLevel(observations);
    } else if (minOrMax == MinOrMax.MAX) {
      highestOrLowestCpotLevel = PainUtils.highestCpotLevel(
          observations);
    }

    if (highestOrLowestCpotLevel != null) {
      result.setPainScore(PainUtils.getPainScoreFromValue(highestOrLowestCpotLevel));
      result.setTimeOfCalculation(timeRange.getEnd());
      result.setStartOfTimePeriod(timeRange.getStart());
      result.setEndOfTimePeriod(timeRange.getEnd());
    }
    return result;
  }
}
