// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.PainUtils;
import com.datafascia.emerge.ucsf.ShiftUtils;
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
  private static Clock clock;

  /**
   * Implements the Pain and Delirium Harm Acceptable Level of Pain (aka Clinician Pain Goal)
   * Provides the numerical pain goal.
   *
   * @param encounterId
   *     encounter to check.
   * @return Numeric pain goal.
   */
  public int getPainGoal(String encounterId) {
    PeriodDt currentOrPriorShift = ShiftUtils.getCurrentOrPreviousShift(clock);
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

    List<Observation> acceptableLevelOfPainAssessments = PainUtils
        .getAcceptableLevelOfPainAssessments(apiClient, encounterId, currentOrPriorShift);

    String freshestType = PainUtils.findFreshestPainType(observationsSinceMidnight);

    Observation acceptableLevelOfPainAssessment = null;

    if (acceptableLevelOfPainAssessments.isEmpty() || !acceptableLevelOfPainAssessments.stream()
        .anyMatch(observation -> !ObservationUtils.getValueAsString(observation).equals(
                "Unable to assess"))) {
      return 11;
    } else if (!acceptableLevelOfPainAssessments.stream().anyMatch(observation
        -> !ObservationUtils.getValueAsString(observation).equals("No Pain"))) {
      return 0;
    }

    if (freshestType.equals("Numerical Level of Pain Assessments")) {
      Observation freshestNumericalPainScore = PainUtils.freshestHighestNumericalPainScore(
          observationsSinceMidnight);

      switch (freshestNumericalPainScore.getCode().getText()) {
        case "Numeric Level of Pain Assessment 1":
          acceptableLevelOfPainAssessment = PainUtils
              .acceptableLevelOfPainAssessment(acceptableLevelOfPainAssessments, "304894105");
          if (acceptableLevelOfPainAssessment != null && PainUtils.getPainScoreFromValue(
              acceptableLevelOfPainAssessment) != null) {
            return PainUtils.getPainScoreFromValue(acceptableLevelOfPainAssessment);
          } else {
            return 11;
          }

        case "Numeric Level of Pain Assessment 2":
          acceptableLevelOfPainAssessment = PainUtils
              .acceptableLevelOfPainAssessment(acceptableLevelOfPainAssessments, "304890004");
          if (acceptableLevelOfPainAssessment != null && PainUtils.getPainScoreFromValue(
              acceptableLevelOfPainAssessment) != null) {
            return PainUtils.getPainScoreFromValue(acceptableLevelOfPainAssessment);
          } else {
            return 11;
          }

        case "Numeric Level of Pain Assessment 3":
          acceptableLevelOfPainAssessment = PainUtils
              .acceptableLevelOfPainAssessment(acceptableLevelOfPainAssessments, "304890005");
          if (acceptableLevelOfPainAssessment != null && PainUtils.getPainScoreFromValue(
              acceptableLevelOfPainAssessment) != null) {
            return PainUtils.getPainScoreFromValue(acceptableLevelOfPainAssessment);
          } else {
            return 11;
          }

        case "Numeric Level of Pain Assessment 4":
          acceptableLevelOfPainAssessment = PainUtils
              .acceptableLevelOfPainAssessment(acceptableLevelOfPainAssessments, "304890006");
          if (acceptableLevelOfPainAssessment != null && PainUtils.getPainScoreFromValue(
              acceptableLevelOfPainAssessment) != null) {
            return PainUtils.getPainScoreFromValue(acceptableLevelOfPainAssessment);
          } else {
            return 11;
          }
      } // end switch

    } else if (freshestType.equals("Verbal Descriptor Level of Pain Assessments")) {
      Observation freshestVerbalPainScore = PainUtils.freshestVerbalPainScore(
          observationsSinceMidnight);
      switch (freshestVerbalPainScore.getCode().getText()) {
        case "Verbal Level of Pain Assessment 1":
          acceptableLevelOfPainAssessment = PainUtils
              .acceptableLevelOfPainAssessment(acceptableLevelOfPainAssessments, "304894105");
          if (acceptableLevelOfPainAssessment != null && PainUtils.getPainScoreFromValue(
              acceptableLevelOfPainAssessment) != null) {
            return PainUtils.getPainScoreFromValue(acceptableLevelOfPainAssessment);
          } else {
            return 11;
          }

        case "Verbal Level of Pain Assessment 2":
          acceptableLevelOfPainAssessment = PainUtils
              .acceptableLevelOfPainAssessment(acceptableLevelOfPainAssessments, "304890004");
          if (acceptableLevelOfPainAssessment != null && PainUtils.getPainScoreFromValue(
              acceptableLevelOfPainAssessment) != null) {
            return PainUtils.getPainScoreFromValue(acceptableLevelOfPainAssessment);
          } else {
            return 11;
          }

        case "Verbal Level of Pain Assessment 3":
          acceptableLevelOfPainAssessment = PainUtils
              .acceptableLevelOfPainAssessment(acceptableLevelOfPainAssessments, "304890005");
          if (acceptableLevelOfPainAssessment != null && PainUtils.getPainScoreFromValue(
              acceptableLevelOfPainAssessment) != null) {
            return PainUtils.getPainScoreFromValue(acceptableLevelOfPainAssessment);
          } else {
            return 11;
          }

        case "Verbal Level of Pain Assessment 4":
          acceptableLevelOfPainAssessment = PainUtils
              .acceptableLevelOfPainAssessment(acceptableLevelOfPainAssessments, "304890006");
          if (acceptableLevelOfPainAssessment != null && PainUtils.getPainScoreFromValue(
              acceptableLevelOfPainAssessment) != null) {
            return PainUtils.getPainScoreFromValue(acceptableLevelOfPainAssessment);
          } else {
            return 11;
          }
      }  // end switch
    }
    return 11;
  }

}
