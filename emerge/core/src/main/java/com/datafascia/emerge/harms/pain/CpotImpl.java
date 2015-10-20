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
 * Implement the Pain and Delirium CPOT Level
 */
public class CpotImpl {
  private enum MinOrMax {
    MIN, MAX
  }

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private static Clock clock;

  /**
   * Result container for the Pain and Delirium CPOT Level (Current)
   */
  @Data @Builder
  public static class CurrentCpotLevel {
    private int painScore;
    private Date timeOfDataAquisition;
  }

  /**
   * Result container for the Pain and Delirium CPOT Level (Minimum or Maximum)
   */
  @Data @Builder
  public static class MinimumOrMaximumCpotLevel {
    private int painScore;
    private Date timeOfCalculation;
    private Date startOfTimePeriod;
    private Date endOfTimePeriod;
  }

  /**
   * Implements the Pain and Delirium CPOT Level (Current)
   * Returns the CPOT level and acquisition time from the newest observation with the highest score.
   *
   * @param encounterId
   *     encounter to check.
   * @return CPOT level and time of acquisition, or {@code null} if not found
   */
  public CurrentCpotLevel getCurrentCpotLevel(String encounterId) {
    CurrentCpotLevel result = CurrentCpotLevel
        .builder()
        .painScore(11)
        .timeOfDataAquisition(null).build();

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

    Observation freshestCpotScore = ObservationUtils.getFreshestByCodeInTimeFrame(apiClient,
        encounterId, ObservationCodeEnum.CPOT.getCode(), sinceMidnight);

    if (freshestCpotScore != null) {
      result.setPainScore(PainUtils.getPainScoreFromValue(freshestCpotScore));
      result.setTimeOfDataAquisition(freshestCpotScore.getIssued());
    }
    return result;
  }

  /**
   * Implements the Pain and Delirium CPOT Level (Low)
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
   * Implements the Pain and Delirium CPOT Level (High)
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
   * Implements the Pain and Delirium CPOT Level (High/Low)
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
    MinimumOrMaximumCpotLevel result = MinimumOrMaximumCpotLevel
        .builder()
        .painScore(11)
        .timeOfCalculation(null)
        .startOfTimePeriod(null)
        .endOfTimePeriod(null).build();

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
      result.setTimeOfCalculation(Date.from(now.toInstant()));
      result.setStartOfTimePeriod(sinceMidnight.getStart());
      result.setEndOfTimePeriod(sinceMidnight.getEnd());
    }
    return result;
  }

}
