// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.emerge.ucsf.ObservationEffectiveComparator;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.base.Strings;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Pain helper methods
 */
@Slf4j
public class PainUtils {

  // Private constructor disallows creating instances of this class.
  private PainUtils() {
  }

  /**
   * Represents what type of pain-related observation is being handled.
   */
  public enum PainType {
    VERBAL,
    NUMERICAL,
    ACCEPTABLE_LEVEL,
    CPOT
  }

  /**
   * Translates the acceptable level of pain value to an integer.
   *
   * @param observation
   *     the pain goal observation
   * @return Pain score from the observation.
   */
  public static int getPainGoal(Observation observation) {
    switch (ObservationUtils.getValueAsString(observation)) {
      case "No pain":
        return 0;
      case "Other (comment)":
        return 11;
      default:
        Integer score = getPainScoreFromValue(observation);
        if (score != null) {
          return score;
        }
    }
    return 11;
  }

  /**
   * Finds freshest observation with a given code and a value that's parseable to integer.
   *
   * @param observations
   *     observations to search
   * @param code
   *     code to search for
   * @param verbalOrNumerical
   *     what type of pain score we're parsing
   * @return Pain score from the freshest applicable observation, 11 if not found.
   */
  public static int acceptableLevelOfPainAssessment(List<Observation> observations,
      String code, PainType verbalOrNumerical) {
    observations.sort(new ObservationEffectiveComparator().reversed());

    for (Observation observation : observations) {
      if (observation.getCode().getCodingFirstRep().getCode().equals(code)) {

        return getPainGoal(observation);
      }
    }
    return 11;
  }

  /**
   * Given a list of observations, returns the freshest of a list of observations
   * that contains one of four codes indicating an acceptable level of pain, aka pain goal.
   * In the event there are multiple simultaneous freshest observations, the one with the highest
   * score is returned.
   *
   * @param observations
   *     observations to search, pre-filtered to pain goal observation codes
   * @return Freshest observation found with the code. {@code null} if not found.
   */
  public static Observation freshestHighestPainGoal(List<Observation> observations) {
    observations.sort(new ObservationEffectiveComparator().reversed());

    Observation result = null;
    ObservationEffectiveComparator comparator = new ObservationEffectiveComparator();

    for (Observation observation : observations) {
      // In the event of simultaneous observations, get the highest pain level of those.
      if (getPainGoal(observation) != 11
          && (result == null || (comparator.compare(observation, result) == 0
          && getPainGoal(observation) > getPainGoal(result)))) {
        result = observation;
      } else if (comparator.compare(observation, result) < 0) {
        // we are now in older observations, give up
        break;
      }
    }

    return result;
  }

  /**
   * Given a list of observations, returns the freshest of a list of observations
   * that contains one of four codes indicating a numerical pain score.
   * In the event there are multiple simultaneous freshest observations, the one with the highest
   * score is returned.
   *
   * @param observations
   *     Observations to search, pre-filtered for appropriate codes.
   * @return Freshest observation found with the code. {@code null} if not found.
   */
  public static Observation freshestHighestNumericalPainScore(List<Observation> observations) {
    observations.sort(new ObservationEffectiveComparator().reversed());

    Observation result = null;
    ObservationEffectiveComparator comparator = new ObservationEffectiveComparator();

    for (Observation observation : observations) {
      // In the event of simultaneous observations, get the highest pain level of those.
      if (getPainScoreFromValue(observation) != null
          && getPainScoreFromValue(observation) != 11
          && (result == null || (comparator.compare(observation, result) == 0
          && getPainScoreFromValue(observation) > getPainScoreFromValue(result)))) {
        result = observation;
      } else if (comparator.compare(observation, result) < 0) {
        // we are now in older observations, give up
        break;
      }
    }

    return result;
  }

  /**
   * Given a list of observations, returns the freshest of a list of observations
   * that contains one of four codes indicating a verbal pain score.
   * In the event there are multiple simultaneous freshest observations, the one with the highest
   * score is returned.
   *
   * @param observations
   *     observations to search
   * @return Freshest observation found with the code. {@code null} if not found.
   */
  public static Observation freshestHighestVerbalPainScore(List<Observation> observations) {
    observations.sort(new ObservationEffectiveComparator().reversed());

    Observation result = null;
    ObservationEffectiveComparator comparator = new ObservationEffectiveComparator();

    for (Observation observation : observations) {
      switch (observation.getCode().getCodingFirstRep().getCode()) {
        case "304890012":
        case "304890013":
        case "304890014":
        case "304890015":
          // In the event of simultaneous observations, get the highest pain level of those.
          if (getVerbalPainScoreFromValue(observation) != null
              && getVerbalPainScoreFromValue(observation) != 11
              && (result == null || (comparator.compare(observation, result) == 0
              && getVerbalPainScoreFromValue(observation) > getVerbalPainScoreFromValue(result)))) {
            result = observation;
          } else if (comparator.compare(observation, result) < 0) {
            // we are now in older observations, give up
            break;
          }
      }
    }

    return result;
  }

  /**
   * Given a list of observations, returns the freshest of a list of observations
   * that contains the appropriate code for CPOT.
   *
   * @param observations
   *     observations to search
   * @return Freshest observation found with the code. {@code null} if not found.
   */
  public static Observation freshestCpot(List<Observation> observations) {
    observations.sort(new ObservationEffectiveComparator().reversed());

    for (Observation observation : observations) {
      if (ObservationCodeEnum.CPOT.isCodeEquals(observation.getCode())) {
        return observation;
      }
    }
    return null;
  }

  /**
   * Given a list of observations, returns the observation with the lowest pain score.
   *
   * @param observations
   *     observations to search
   * @return Lowest scoring observation found with the code. {@code null} if not found.
   */
  public static Observation lowestNumericalPainScore(List<Observation> observations) {

    Observation result = null;

    for (Observation observation : observations) {
      switch (observation.getCode().getCodingFirstRep().getCode()) {
        case "304890008":
        case "304890009":
        case "304890010":
        case "304890011":
          if (getPainScoreFromValue(observation) != null && (result == null
              || getPainScoreFromValue(observation) < getPainScoreFromValue(result))) {
            result = observation;
          }
      }
    }

    return result;
  }

  /**
   * Given a list of observations, returns the observation with the highest pain score.
   *
   * @param observations
   *     observations to search
   * @return Lowest scoring observation found with the code. {@code null} if not found.
   */
  public static Observation highestNumericalPainScore(List<Observation> observations) {

    Observation result = null;

    for (Observation observation : observations) {
      switch (observation.getCode().getCodingFirstRep().getCode()) {
        case "304890008":
        case "304890009":
        case "304890010":
        case "304890011":
          if (result == null || (getPainScoreFromValue(observation) != null
              && getPainScoreFromValue(observation) > getPainScoreFromValue(result))) {
            result = observation;
          }
      }
    }

    return result;
  }

  /**
   * Given a list of observations, returns the observation with the lowest CPOT level.
   *
   * @param observations
   *     observations to search
   * @return Lowest scoring observation found with the code. {@code null} if not found.
   */
  public static Observation lowestCpotLevel(List<Observation> observations) {
    Observation result = null;

    for (Observation observation : observations) {
      if (observation.getCode().getCodingFirstRep().getCode()
          .equals(ObservationCodeEnum.CPOT.getCode())) {
        if (result == null || (getPainScoreFromValue(observation) != null
            && getPainScoreFromValue(observation) < getPainScoreFromValue(result))) {
          result = observation;
        }
      }
    }

    return result;
  }

  /**
   * Given a list of observations, returns the observation with the highest CPOT level.
   *
   * @param observations
   *     observations to search
   * @return Lowest scoring observation found with the code. {@code null} if not found.
   */
  public static Observation highestCpotLevel(List<Observation> observations) {
    Observation result = null;

    for (Observation observation : observations) {
      if (observation.getCode().getCodingFirstRep().getCode()
          .equals(ObservationCodeEnum.CPOT.getCode())) {
        if (result == null || (getPainScoreFromValue(observation) != null
            && getPainScoreFromValue(observation) > getPainScoreFromValue(result))) {
          result = observation;
        }
      }
    }

    return result;
  }

  /**
   * Attempts to pull the value of an Observation and parse it to a pain score integer that is
   * between 0 and 11. If no such integer can be parsed, null is returned.
   *
   * @param observation
   *     An observation containing a pain score in its value field.
   * @return The integer pain score from the observation or  {@code null} if not found.
   */
  public static Integer getPainScoreFromValue(Observation observation) {
    if (observation == null || observation.getValue() == null) {
      return null;
    }

    int painScore = -1;

    if (observation.getValue() instanceof QuantityDt) {
      QuantityDt value = (QuantityDt) observation.getValue();
      painScore = value.getValue().intValueExact();
    } else if (observation.getValue() instanceof StringDt) {
      if (ObservationUtils.getValueAsString(observation).equals("Unable to Assess")) {
        return 11;
      }
      try {
        painScore = Integer.parseInt(ObservationUtils.getValueAsString(observation));
      } catch (NumberFormatException ex) {
        log.warn("Non-numeric pain score value:" + ObservationUtils.getValueAsString(observation)
            + " found in observation: " + observation.getId().getValueAsString());
        return null;
      }
    } else {
      log.warn("Unexpected type of pain score value:" + observation.getValue()
          + " found in observation: " + observation.getId().getValueAsString());
      return null;
    }

    if (painScore >= 0 && painScore <= 11) {
      return painScore;
    } else {
      log.warn("Unexpected quantiy of pain score value:" + painScore + " found in observation: "
          + observation.getId().getValueAsString());
      return null;
    }
  }

  /**
   * Finds the highest verbal pain level in observations.
   *
   * @param observations
   *     observations to search
   * @return Observation with the highest numerical level of pain found in verbal observations.
   */
  public static Observation highestVerbalPainScore(List<Observation> observations) {

    Observation result = null;

    for (Observation observation : observations) {
      switch (observation.getCode().getCodingFirstRep().getCode()) {
        case "304890012":
        case "304890013":
        case "304890014":
        case "304890015":
          if ((getVerbalPainScoreFromValue(observation) != null
              && getVerbalPainScoreFromValue(observation) != 11) && (result == null
              || getVerbalPainScoreFromValue(observation) > getVerbalPainScoreFromValue(result))) {
            result = observation;
          }
      }
    }

    return result;
  }

  /**
   * Finds the lowest verbal pain level in observations.
   *
   * @param observations
   *     observations to search
   * @return Observation with the lowest numerical level of pain found in verbal observations.
   */
  public static Observation lowestVerbalPainScore(List<Observation> observations) {

    Observation result = null;

    for (Observation observation : observations) {
      switch (observation.getCode().getCodingFirstRep().getCode()) {
        case "304890012":
        case "304890013":
        case "304890014":
        case "304890015":
          if (getVerbalPainScoreFromValue(observation) != null && (result == null
              || getVerbalPainScoreFromValue(
                  observation) < getVerbalPainScoreFromValue(result))) {
            result = observation;
          }
      }
    }

    return result;
  }

  /**
   * Translates verbal descriptors of pain assessments to numerical values.
   *
   * @param observation
   *     An observation containing a verbal descriptor of pain assessment.
   * @return
   *     Numerical value of pain assessment, or {@code null} if not found
   */
  public static Integer getVerbalPainScoreFromValue(Observation observation) {
    if (observation == null || Strings.isNullOrEmpty(
        ObservationUtils.getValueAsString(observation))) {
      return null;
    }
    switch (ObservationUtils.getValueAsString(observation)) {
      case "None":
        return 0;
      case "Mild":
        return 1;
      case "Moderate":
        return 5;
      case "Severe":
        return 7;
      default:
        log.warn("Unexpected verbal pain level found: " + ObservationUtils.getValueAsString(
            observation));
        return null;
    }
  }
}
