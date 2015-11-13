// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.google.common.base.Strings;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

/**
 * Implements the Pain and Delirium Verbal Pain Level
 */
@Slf4j
public class VerbalPainLevel {
  private enum MinOrMax {
    MIN, MAX
  }

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

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
   * @param encounterId
   *     encounter to check.
   * @return Numeric pain level and time of acquisition, or {@code null} if not found
   */
  public CurrentPainLevel getCurrentPainLevel(String encounterId) {
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

    CurrentPainLevel result = CurrentPainLevel
        .builder()
        .painScore(11)
        .timeOfDataAquisition(sinceMidnight.getEnd())
        .build();

    List<Observation> observationsSinceMidnight = ObservationUtils.searchByTimeFrame(apiClient,
        encounterId, sinceMidnight);

    Observation freshestVerbalPainScore = PainUtils.freshestHighestVerbalPainScore(
        observationsSinceMidnight);

    if (freshestVerbalPainScore != null && !Strings.isNullOrEmpty(
        ObservationUtils.getValueAsString(freshestVerbalPainScore))) {
      Integer painScore = PainUtils.getVerbalPainScoreFromValue(freshestVerbalPainScore);
      result.setPainScore((painScore != null) ? painScore : 11);
      result.setTimeOfDataAquisition(ObservationUtils.getEffectiveDate(freshestVerbalPainScore));
    } else {
      log.info("verbal pain score for encounter ID [{}] not found", encounterId);
    }
    return result;
  }

  /**
   * Implements the Pain and Delirium Verbal Pain Level (Low)
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
   * Implements the Pain and Delirium Verbal Pain Level (High)
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
   * Implements the Pain and Delirium Verbal Pain Level (High/Low)
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

    MinimumOrMaximumPainLevel result = MinimumOrMaximumPainLevel
        .builder()
        .painScore(11)
        .timeOfCalculation(sinceMidnight.getEnd())
        .startOfTimePeriod(sinceMidnight.getStart())
        .endOfTimePeriod(sinceMidnight.getEnd())
        .build();

    List<Observation> observationsSinceMidnight = ObservationUtils.searchByTimeFrame(apiClient,
        encounterId, sinceMidnight);

    Observation highestOrLowestVerbalPainScore = null;

    if (minOrMax == MinOrMax.MIN) {
      highestOrLowestVerbalPainScore = PainUtils.lowestVerbalPainScore(
          observationsSinceMidnight);
    } else if (minOrMax == MinOrMax.MAX) {
      highestOrLowestVerbalPainScore = PainUtils.highestVerbalPainScore(
          observationsSinceMidnight);
    }

    if (highestOrLowestVerbalPainScore != null) {
      result.setPainScore(PainUtils.getVerbalPainScoreFromValue(highestOrLowestVerbalPainScore));
      result.setTimeOfCalculation(sinceMidnight.getEnd());
      result.setStartOfTimePeriod(sinceMidnight.getStart());
      result.setEndOfTimePeriod(sinceMidnight.getEnd());
    }
    return result;
  }

}
