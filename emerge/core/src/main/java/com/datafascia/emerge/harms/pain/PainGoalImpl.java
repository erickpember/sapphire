// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.Periods;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

/**
 * Implements the Pain and Delirium Harm Acceptable Level of Pain (aka Clinician Pain Goal)
 */
public class PainGoalImpl {

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  private static final Set<String> NUMERICAL_PAIN_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.NUMERICAL_PAIN_01.getCode(),
      ObservationCodeEnum.NUMERICAL_PAIN_02.getCode(),
      ObservationCodeEnum.NUMERICAL_PAIN_03.getCode(),
      ObservationCodeEnum.NUMERICAL_PAIN_04.getCode());

  private static final Set<String> VERBAL_PAIN_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.VERBAL_PAIN_01.getCode(),
      ObservationCodeEnum.VERBAL_PAIN_02.getCode(),
      ObservationCodeEnum.VERBAL_PAIN_03.getCode(),
      ObservationCodeEnum.VERBAL_PAIN_04.getCode());

  private static final Set<String> PAIN_GOAL_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.PAIN_GOAL_01.getCode(),
      ObservationCodeEnum.PAIN_GOAL_02.getCode(),
      ObservationCodeEnum.PAIN_GOAL_03.getCode(),
      ObservationCodeEnum.PAIN_GOAL_04.getCode());

  /**
   * Implements the Pain and Delirium Harm Acceptable Level of Pain (AKA Clinician Pain Goal)
   * Provides the numerical pain goal.
   *
   * @param encounterId
   *     encounter to check.
   * @return Numeric pain goal.
   */
  public int getPainGoal(String encounterId) {
    PeriodDt thirteenHoursAgo = Periods.getPastHoursToNow(clock, 13);
    PeriodDt sinceMidnight = Periods.getMidnightToNow(clock);

    Observations observations = apiClient.getObservationClient().list(encounterId);

    return getPainGoal(observations, thirteenHoursAgo, sinceMidnight);
  }

  /**
   * Implements the Pain and Delirium Harm Acceptable Level of Pain (AKA Clinician Pain Goal)
   * Provides the numerical pain goal.
   *
   * @param observations
   *     Observations for the encounter.
   * @param lastThirteenHours
   *     Look-back period for acceptable level of pain assessments.
   * @param sinceMidnight
   *     Look-back period for min-max pain levels.
   * @return Numeric pain goal.
   */
  public int getPainGoal(Observations observations, PeriodDt lastThirteenHours,
      PeriodDt sinceMidnight) {
    PainUtils.PainType freshestPainType = null;

    Observation freshestNumericalScore = PainUtils.freshestHighestNumericalPainScore(
        observations.list(NUMERICAL_PAIN_OBSERVATION_CODES, sinceMidnight.getStart().toInstant(),
            sinceMidnight.getEnd().toInstant()));
    Observation freshestVerbalScore = PainUtils.freshestHighestVerbalPainScore(
        observations.list(VERBAL_PAIN_OBSERVATION_CODES, sinceMidnight.getStart().toInstant(),
            sinceMidnight.getEnd().toInstant()));

    if (ObservationUtils.firstIsFresher(freshestVerbalScore, freshestNumericalScore)) {
      freshestPainType = PainUtils.PainType.VERBAL;
    } else if (ObservationUtils.firstIsFresher(freshestNumericalScore, freshestVerbalScore)) {
      freshestPainType = PainUtils.PainType.NUMERICAL;
    }

    List<Observation> acceptableLevelOfPainAssessments = observations.list(
        PAIN_GOAL_OBSERVATION_CODES, lastThirteenHours.getStart().toInstant(),
        lastThirteenHours.getEnd().toInstant());

    if (acceptableLevelOfPainAssessments.isEmpty() || !acceptableLevelOfPainAssessments.stream()
        .anyMatch(observation -> !ObservationUtils.getValueAsString(observation).equals(
                "Unable to assess"))) {
      return 11;
    } else if (!acceptableLevelOfPainAssessments.stream().anyMatch(observation
        -> !ObservationUtils.getValueAsString(observation).equals("No Pain"))) {
      return 0;
    }

    Observation freshestPainGoal = PainUtils.freshestHighestPainGoal(
        acceptableLevelOfPainAssessments);

    Observation freshestScore = null;

    if (freshestPainType != null) {
      if (freshestPainType.equals(PainUtils.PainType.NUMERICAL)) {
        freshestScore = freshestNumericalScore;
      } else if (freshestPainType.equals(PainUtils.PainType.VERBAL)) {
        freshestScore = freshestVerbalScore;
      }
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
    } else if (freshestPainGoal != null) {
      // No numerical or verbal pain score since midnight so
      // take the freshest pain goal from the last 13 hours.
      return PainUtils.getPainGoal(freshestPainGoal);
    }

    return 11;
  }
}
