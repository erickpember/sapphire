// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.rass;

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
 * Implement the Pain and Delirium Current, Minimum and Maximum RASS Levels
 */
public class RassLevelsImpl {
  private enum MinOrMax {
    MIN, MAX
  }

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private static Clock clock;

  /**
   * Result container for the Pain and Delirium RASS Level (Current)
   */
  @Data @Builder
  public static class CurrentRassLevel {
    private int rassScore;
    private Date timeOfDataAquisition;
  }

  /**
   * Result container for the Pain and Delirium RASS Level (Minimum or Maximum)
   */
  @Data @Builder
  public static class MinimumOrMaximumRassLevel {
    private int rassScore;
    private Date timeOfCalculation;
    private Date startOfTimePeriod;
    private Date endOfTimePeriod;
  }

  /**
   * Implements the Pain and Delirium RASS Level (Current)
   * Returns the RASS level and acquisition time from the newest RASS-coded observation.
   *
   * @param encounterId
   *     encounter to check.
   * @return RASS level and time of acquisition, or {@code null} if not found
   */
  public CurrentRassLevel getCurrentRassLevel(String encounterId) {
    CurrentRassLevel result = CurrentRassLevel
        .builder()
        .rassScore(11)
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

    Observation freshestRassScore = ObservationUtils.getFreshestByCodeInTimeFrame(apiClient,
        encounterId, ObservationCodeEnum.RASS.getCode(), sinceMidnight);

    if (freshestRassScore != null) {
      result.setRassScore(RassUtils.getRassScoreFromValue(freshestRassScore));
      result.setTimeOfDataAquisition(freshestRassScore.getIssued());
    }
    return result;
  }

  /**
   * Implements the Pain and Delirium RASS Level (Low)
   * Returns the lowest RASS level from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param encounterId
   *     encounter to check.
   * @return RASS level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumRassLevel getRassMin(String encounterId) {
    return getDailyMinOrMax(encounterId, MinOrMax.MIN);
  }

  /**
   * Implements the Pain and Delirium RASS Level (High)
   * Returns the highest RASS level from a period of between midnight and now, along with the
   * same period and a calculation time of now.
   *
   * @param encounterId
   *     encounter to check.
   * @return RASS level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  public MinimumOrMaximumRassLevel getRassMax(String encounterId) {
    return getDailyMinOrMax(encounterId, MinOrMax.MAX);
  }

  /**
   * Implements the Pain and Delirium RASS Level (High/Low)
   * Returns the RASS level and acquisition time from the newest observation with
   * either the highest or lowest level, depending on MinOrMax.
   *
   * @param encounterId
   *     encounter to check.
   * @param minOrMax
   *     Whether to return the highest or lowest RASS level.
   * @return RASS level, along with the period between midnight and now,
   *     or {@code null} if not found
   */
  private MinimumOrMaximumRassLevel getDailyMinOrMax(String encounterId, MinOrMax minOrMax) {
    MinimumOrMaximumRassLevel result = MinimumOrMaximumRassLevel
        .builder()
        .rassScore(11)
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

    Observation highestOrLowestRassLevel = null;

    if (minOrMax == MinOrMax.MIN) {
      highestOrLowestRassLevel = RassUtils.lowestRassLevel(observationsSinceMidnight);
    } else if (minOrMax == MinOrMax.MAX) {
      highestOrLowestRassLevel = RassUtils.highestRassLevel(
          observationsSinceMidnight);
    }

    if (highestOrLowestRassLevel != null) {
      result.setRassScore(RassUtils.getRassScoreFromValue(highestOrLowestRassLevel));
      result.setTimeOfCalculation(Date.from(now.toInstant()));
      result.setStartOfTimePeriod(sinceMidnight.getStart());
      result.setEndOfTimePeriod(sinceMidnight.getEnd());
    }
    return result;
  }

}
