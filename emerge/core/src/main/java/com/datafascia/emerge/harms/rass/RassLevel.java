// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.rass;

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
import java.util.Optional;
import java.util.stream.Collectors;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Data;

/**
 * Implement the pain and delirium current, minimum and maximum RASS levels
 */
public class RassLevel {
  private enum MinOrMax {
    MIN, MAX
  }

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  private static final int RASS_LEVEL_LOOKBACK = 7;

  /**
   * Result container for all of the pain and delirium RASS level (current + minimum + maximum)
   */
  @Data @Builder
  public static class AllRassLevels {
    private CurrentRassLevel current;
    private MinimumOrMaximumRassLevel min;
    private MinimumOrMaximumRassLevel max;
  }

  /**
   * Result container for the pain and delirium RASS level (current)
   */
  @Data @Builder
  public static class CurrentRassLevel {
    private int rassScore;
    private Date timeOfDataAquisition;
  }

  /**
   * Result container for the pain and delirium RASS level (minimum or maximum)
   */
  @Data @Builder
  public static class MinimumOrMaximumRassLevel {
    private int rassScore;
    private Date timeOfCalculation;
    private Date startOfTimePeriod;
    private Date endOfTimePeriod;
  }

  /**
   * Checks if observation is relevant to RASS and within the necessary time window.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to RASS.
   */
  public static boolean isRelevant(Observation observation) {
    Clock clock = Injectors.getInjector().getInstance(Clock.class);
    PeriodDt currentRassTimeRange = Periods.getPastHoursToNow(clock, RASS_LEVEL_LOOKBACK);
    PeriodDt rassMinMaxTimeRange = Periods.getMidnightToNow(clock);

    return (ObservationUtils.isAfter(
        observation, currentRassTimeRange.getStart()) || ObservationUtils.isAfter(
            observation, rassMinMaxTimeRange.getStart()))
        && ObservationCodeEnum.RASS.isCodeEquals(
            observation.getCode());
  }

  /**
   * Wraps implementation for all pain and delirium RASS levels (current, minimum, maximum)
   *
   * @param encounterId
   *     encounter to check.
   * @return Current, minimum and maximum RASS levels.
   */
  public AllRassLevels getAllRassLevels(String encounterId) {
    PeriodDt currentRassTimeRange = Periods.getPastHoursToNow(clock, RASS_LEVEL_LOOKBACK);
    PeriodDt rassMinMaxTimeRange = Periods.getMidnightToNow(clock);
    Observations observations = apiClient.getObservationClient().list(encounterId);

    return getAllRassLevels(observations, currentRassTimeRange, rassMinMaxTimeRange);
  }

  /**
   * Wraps implementation for all pain and delirium RASS levels (current, minimum, maximum)
   * Handles all the observation filtering required.
   *
   * @param observations
   *     Observations for the encounter.
   * @param currentRassTimeRange
   *     Time range for the current RASS level.
   * @param rassMinMaxTimeRange
   *     Time range for the RASS min and max.
   * @return Current, minimum and maximum RASS levels.
   */
  public AllRassLevels getAllRassLevels(Observations observations, PeriodDt currentRassTimeRange,
      PeriodDt rassMinMaxTimeRange) {
    Instant lowestTimeBound = null;
    if (currentRassTimeRange.getStart().after(rassMinMaxTimeRange.getStart())) {
      lowestTimeBound = rassMinMaxTimeRange.getStart().toInstant();
    } else {
      lowestTimeBound = currentRassTimeRange.getStart().toInstant();
    }

    List<Observation> recentRassObservations = observations.list(ObservationCodeEnum.RASS.getCode(),
        lowestTimeBound, null);

    List<Observation> rassLevelObservations = recentRassObservations
        .stream()
        .filter(obs -> ObservationUtils.insideTimeFrame(obs, currentRassTimeRange))
        .collect(Collectors.toList());
    CurrentRassLevel currentLevel = getCurrentRassLevel(rassLevelObservations,
        currentRassTimeRange);

    List<Observation> rassMinMaxObservations = recentRassObservations
        .stream()
        .filter(obs -> ObservationUtils.insideTimeFrame(obs, rassMinMaxTimeRange))
        .collect(Collectors.toList());
    MinimumOrMaximumRassLevel maxLevel = getRassMax(rassMinMaxObservations, rassMinMaxTimeRange);
    MinimumOrMaximumRassLevel minLevel = getRassMin(rassMinMaxObservations, rassMinMaxTimeRange);

    return AllRassLevels.builder()
        .current(currentLevel)
        .min(minLevel)
        .max(maxLevel)
        .build();
  }

  /**
   * Implements the pain and delirium RASS level (current)
   * Returns the RASS level and acquisition time from the newest RASS-coded observation.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return RASS level and time of acquisition, or {@code null} if not found
   */
  private CurrentRassLevel getCurrentRassLevel(List<Observation> observations, PeriodDt timeRange) {

    CurrentRassLevel result = CurrentRassLevel.builder()
        .rassScore(11)
        .timeOfDataAquisition(timeRange.getStart())
        .build();

    Optional<Observation> freshestRassScore = observations.stream()
        .max(Observations.EFFECTIVE_COMPARATOR);

    if (freshestRassScore.isPresent()) {
      result.setRassScore(RassUtils.getRassScoreFromValue(freshestRassScore.get()));
      result.setTimeOfDataAquisition(ObservationUtils.getEffectiveDate(freshestRassScore.get()));
    }
    return result;
  }

  /**
   * Implements the pain and delirium RASS level (low)
   * Returns the lowest RASS level from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return RASS level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  private MinimumOrMaximumRassLevel getRassMin(List<Observation> observations, PeriodDt timeRange) {
    return getDailyMinOrMax(observations, timeRange, MinOrMax.MIN);
  }

  /**
   * Implements the pain and delirium RASS level (high)
   * Returns the highest RASS level from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @return RASS level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  private MinimumOrMaximumRassLevel getRassMax(List<Observation> observations, PeriodDt timeRange) {
    return getDailyMinOrMax(observations, timeRange, MinOrMax.MAX);
  }

  /**
   * Implements the pain and delirium RASS level (high/low)
   * Returns the RASS level and acquisition time from the newest observation with
   * either the highest or lowest level, depending on MinOrMax.
   *
   * @param observations
   *     The relevant observations for an encounter.
   * @param timeRange
   *     The time bound for this search.
   * @param minOrMax
   *     Whether to return the min or max.
   * @return RASS level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  private MinimumOrMaximumRassLevel getDailyMinOrMax(List<Observation> observations,
      PeriodDt timeRange,
      MinOrMax minOrMax) {
    MinimumOrMaximumRassLevel result = MinimumOrMaximumRassLevel
        .builder()
        .rassScore(11)
        .timeOfCalculation(timeRange.getEnd())
        .startOfTimePeriod(timeRange.getStart())
        .endOfTimePeriod(timeRange.getEnd()).build();

    Observation highestOrLowestRassLevel = null;

    if (minOrMax == MinOrMax.MIN) {
      highestOrLowestRassLevel = RassUtils.lowestRassLevel(observations);
    } else if (minOrMax == MinOrMax.MAX) {
      highestOrLowestRassLevel = RassUtils.highestRassLevel(
          observations);
    }

    if (highestOrLowestRassLevel != null) {
      result.setRassScore(RassUtils.getRassScoreFromValue(highestOrLowestRassLevel));
      result.setTimeOfCalculation(timeRange.getEnd());
      result.setStartOfTimePeriod(timeRange.getStart());
      result.setEndOfTimePeriod(timeRange.getEnd());
    }
    return result;
  }
}
