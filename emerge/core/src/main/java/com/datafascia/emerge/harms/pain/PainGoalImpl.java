// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.ShiftUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/**
 * Implements the Pain and Delirium Harm Acceptable Level of Pain (aka Clinician Pain Goal)
 */
public class PainGoalImpl {

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  /**
   * Implements the Pain and Delirium Harm Acceptable Level of Pain (aka Clinician Pain Goal)
   * Provides the numerical pain goal.
   *
   * @param encounterId
   *     encounter to check.
   * @return Numeric pain goal.
   */
  public int getPainGoal(String encounterId) {
    PeriodDt fromCurrentOrPriorShift = ShiftUtils.getCurrentOrPriorShiftToNow(clock);
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



    PainUtils.PainType freshestPainType = null;

    Observation freshestNumericalScore = PainUtils.freshestHighestNumericalPainScore(
        observationsSinceMidnight);
    Observation freshestVerbalScore = PainUtils.freshestHighestVerbalPainScore(
        observationsSinceMidnight);

    if (ObservationUtils.firstIsFresher(freshestVerbalScore, freshestNumericalScore)) {
      freshestPainType = PainUtils.PainType.VERBAL;
    } else if (ObservationUtils.firstIsFresher(freshestNumericalScore, freshestVerbalScore)) {
      freshestPainType = PainUtils.PainType.NUMERICAL;
    } else {
      // No pain type found
      return 11;
    }


    Observation freshestScore = null;

    List<Observation> acceptableLevelOfPainAssessments = PainUtils
        .getAcceptableLevelOfPainAssessments(apiClient, encounterId, fromCurrentOrPriorShift);

    if (acceptableLevelOfPainAssessments.isEmpty() || !acceptableLevelOfPainAssessments.stream()
        .anyMatch(observation -> !ObservationUtils.getValueAsString(observation).equals(
                "Unable to assess"))) {
      return 11;
    } else if (!acceptableLevelOfPainAssessments.stream().anyMatch(observation
        -> !ObservationUtils.getValueAsString(observation).equals("No Pain"))) {
      return 0;
    }

    if (freshestPainType.equals(PainUtils.PainType.NUMERICAL)) {
      freshestScore = freshestNumericalScore;
    } else if (freshestPainType.equals(PainUtils.PainType.VERBAL)) {
      freshestScore = freshestVerbalScore;
    }

    if (freshestScore != null) {
      if (ObservationCodeEnum.NUMERICAL_PAIN_01.isCodeEquals(freshestScore.getCode())
          || ObservationCodeEnum.VERBAL_PAIN_01.isCodeEquals(freshestScore.getCode())) {

        return PainUtils.acceptableLevelOfPainAssessment(
            acceptableLevelOfPainAssessments,
            ObservationCodeEnum.PAIN_GOAL_01.getCode(),
            freshestPainType);
      } else if (ObservationCodeEnum.NUMERICAL_PAIN_02.isCodeEquals(freshestScore.getCode())
          || ObservationCodeEnum.VERBAL_PAIN_02.isCodeEquals(freshestScore.getCode())) {
        return PainUtils.acceptableLevelOfPainAssessment(
            acceptableLevelOfPainAssessments,
            ObservationCodeEnum.PAIN_GOAL_02.getCode(),
            freshestPainType);
      } else if (ObservationCodeEnum.NUMERICAL_PAIN_03.isCodeEquals(freshestScore.getCode())
          || ObservationCodeEnum.VERBAL_PAIN_03.isCodeEquals(freshestScore.getCode())) {
        return PainUtils.acceptableLevelOfPainAssessment(
            acceptableLevelOfPainAssessments,
            ObservationCodeEnum.PAIN_GOAL_03.getCode(),
            freshestPainType);
      } else if (ObservationCodeEnum.NUMERICAL_PAIN_04.isCodeEquals(freshestScore.getCode())
          || ObservationCodeEnum.VERBAL_PAIN_04.isCodeEquals(freshestScore.getCode())) {
        return PainUtils.acceptableLevelOfPainAssessment(
            acceptableLevelOfPainAssessments,
            ObservationCodeEnum.PAIN_GOAL_04.getCode(),
            freshestPainType);
      }
    } // end if freshestScore is not null

    return 11;
  }

}
