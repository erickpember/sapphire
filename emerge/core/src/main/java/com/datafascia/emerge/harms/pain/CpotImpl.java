// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
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
   * Checks if observation is relevant to CPOT.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to CPOT.
   */
  public static boolean isRelevant(Observation observation) {
    return ObservationCodeEnum.CPOT.isCodeEquals(observation.getCode());
  }

  /**
   * Implements the pain and delirium CPOT level (current)
   * Returns the CPOT level and acquisition time from the newest observation with the highest score.
   *
   * @param encounterId
   *     encounter to check.
   * @return CPOT level and time of acquisition, or {@code null} if not found
   */
  public CurrentCpotLevel getCurrentCpotLevel(String encounterId) {
    ZonedDateTime now = ZonedDateTime.now(clock);
    ZonedDateTime midnight = ZonedDateTime.of(
        now.getYear(),
        now.getMonthValue(),
        now.getDayOfMonth(),
        0,
        0,
        0,
        0,
        clock.getZone());
    PeriodDt sinceMidnight = new PeriodDt();
    sinceMidnight.setStart(new DateTimeDt(Date.from(midnight.toInstant())));
    sinceMidnight.setEnd(new DateTimeDt(Date.from(now.toInstant())));

    CurrentCpotLevel result = CurrentCpotLevel
        .builder()
        .painScore(11)
        .timeOfDataAquisition(sinceMidnight.getEnd())
        .build();

    Observation freshestCpotScore = ObservationUtils.getFreshestByCodeInTimeFrame(apiClient,
        encounterId, ObservationCodeEnum.CPOT.getCode(), sinceMidnight);

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
   * @param encounterId
   *     encounter to check.
   * @return CPOT level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumCpotLevel getCpotMin(String encounterId) {
    return getDailyMinOrMax(encounterId, MinOrMax.MIN);
  }

  /**
   * Implements the pain and delirium CPOT level (high)
   * Returns the highest CPOT level from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param encounterId
   *     encounter to check.
   * @return CPOT level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumCpotLevel getCpotMax(String encounterId) {
    return getDailyMinOrMax(encounterId, MinOrMax.MAX);
  }

  /**
   * Implements the pain and delirium CPOT level (high/low)
   * Returns the CPOT level and acquisition time from the newest observation with
   * either the highest or lowest level, depending on MinOrMax.
   *
   * @param encounterId
   *     encounter to check.
   * @param minOrMax
   *     Whether to return the highest or lowest CPOT level.
   * @return CPOT level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  private MinimumOrMaximumCpotLevel getDailyMinOrMax(String encounterId, MinOrMax minOrMax) {
    ZonedDateTime now = ZonedDateTime.now(clock);
    ZonedDateTime midnight = ZonedDateTime.of(
        now.getYear(),
        now.getMonthValue(),
        now.getDayOfMonth(),
        0,
        0,
        0,
        0,
        clock.getZone());
    PeriodDt sinceMidnight = new PeriodDt();
    sinceMidnight.setStart(new DateTimeDt(Date.from(midnight.toInstant())));
    sinceMidnight.setEnd(new DateTimeDt(Date.from(now.toInstant())));

    MinimumOrMaximumCpotLevel result = MinimumOrMaximumCpotLevel
        .builder()
        .painScore(11)
        .timeOfCalculation(sinceMidnight.getEnd())
        .startOfTimePeriod(sinceMidnight.getStart())
        .endOfTimePeriod(sinceMidnight.getEnd())
        .build();

    List<Observation> observationsSinceMidnight = ObservationUtils.searchByTimeFrame(apiClient,
        encounterId, sinceMidnight);

    Observation highestOrLowestCpotLevel = null;

    if (minOrMax == MinOrMax.MIN) {
      highestOrLowestCpotLevel = PainUtils.lowestCpotLevel(observationsSinceMidnight);
    } else if (minOrMax == MinOrMax.MAX) {
      highestOrLowestCpotLevel = PainUtils.highestCpotLevel(
          observationsSinceMidnight);
    }

    if (highestOrLowestCpotLevel != null) {
      result.setPainScore(PainUtils.getPainScoreFromValue(highestOrLowestCpotLevel));
      result.setTimeOfCalculation(sinceMidnight.getEnd());
      result.setStartOfTimePeriod(sinceMidnight.getStart());
      result.setEndOfTimePeriod(sinceMidnight.getEnd());
    }
    return result;
  }

}
