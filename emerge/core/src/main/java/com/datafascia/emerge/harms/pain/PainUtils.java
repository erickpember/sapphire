// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationEffectiveComparator;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.base.Strings;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
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
   * Finds freshest pain type.
   *
   * @param observations
   *     observations to search
   * @return freshest pain type, or {@code null} if no pain observations found
   */
  public static String findFreshestPainType(List<Observation> observations) {
    observations.sort(new ObservationEffectiveComparator().reversed());

    for (Observation observation : observations) {
      String code = observation.getCode().getCodingFirstRep().getCode();
      switch (code) {
        case "304890008":
        case "304890009":
        case "304890010":
        case "304890011":
          return "Numerical Level of Pain Assessments";

        case "304894105":
        case "304890004":
        case "304890005":
        case "304890006":
          return "Acceptable Level of Pain Assessments";

        case "304890012":
        case "304890013":
        case "304890014":
        case "304890015":
          return "Verbal Descriptor Level of Pain Assessments";

        case "304890016":
          return "Critical-Care Pain Observation Tool (CPOT) Total";
      }
    }

    return null;
  }

  /**
   * Finds freshest observation with a given code and a value that's parseable to integer.
   *
   * @param observations
   *     observations to search
   * @param code
   *     code to search for
   * @return Freshest observation found with the code and an integer parseable value.
   *    {@code null} if not found.
   */
  public static Observation acceptableLevelOfPainAssessment(List<Observation> observations,
      String code) {
    observations.sort(new ObservationEffectiveComparator().reversed());

    for (Observation observation : observations) {
      if (observation.getCode().getCodingFirstRep().getCode().equals(code)) {
        if (!Strings.isNullOrEmpty(ObservationUtils.getValueAsString(observation))
            || getPainScoreFromValue(observation) != null) {
          return observation;
        }
      }
    }
    return null;
  }

  /**
   * Given a list of observations, returns the freshest of a list of observations
   * that contains one of four codes indicating a numerical pain score.
   * In the event there are multiple simultaneous freshest observations, the one with the highest
   * score is returned.
   *
   * @param observations
   *     observations to search
   * @return Freshest observation found with the code. {@code null} if not found.
   */
  public static Observation freshestHighestNumericalPainScore(List<Observation> observations) {
    observations.sort(new ObservationEffectiveComparator().reversed());

    Observation result = null;
    ObservationEffectiveComparator comparator = new ObservationEffectiveComparator();

    for (Observation observation : observations) {
      switch (observation.getCode().getCodingFirstRep().getCode()) {
        case "304890008":
        case "304890009":
        case "304890010":
        case "304890011":
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
   * Given a list of observations, returns the freshest of a list of observations
   * that contains one of four codes indicating a verbal pain score.
   *
   * @param observations
   *     observations to search
   * @return Freshest observation found with the code.  {@code null} if not found.
   */
  public static Observation freshestVerbalPainScore(List<Observation> observations) {
    observations.sort(new ObservationEffectiveComparator().reversed());

    for (Observation observation : observations) {
      switch (observation.getCode().getCodingFirstRep().getCode()) {
        case "304890012":
        case "304890013":
        case "304890014":
        case "304890015":
          return observation;
      }
    }

    return null;
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
   * Returns Observations from a time window with codes that match acceptable level of pain
   * assessments.
   * @param apiClient
   *     API client
   * @param encounterId
   *     Relevant encounter for search.
   * @param currentOrPriorShift
   *     Relevant time period for search.
   * @return Acceptable level of pain assessment observations.
   */
  public static List<Observation> getAcceptableLevelOfPainAssessments(ClientBuilder apiClient,
      String encounterId, PeriodDt currentOrPriorShift) {
    List<String> codes = Arrays.asList("304894105", "304890004", "304890005", "304890006");

    return ObservationUtils.searchByTimeFrame(apiClient, encounterId, currentOrPriorShift)
        .stream()
        .filter(observation -> codes.contains(observation.getCode().getCodingFirstRep().getCode()))
        .collect(Collectors.toList());
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
