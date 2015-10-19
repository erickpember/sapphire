// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
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
          if (result == null || (getPainScoreFromValue(observation) != null && comparator.compare(
              observation, result) == 0 && getPainScoreFromValue(observation)
              > getPainScoreFromValue(result))) {
            result = observation;
          } else if (comparator.compare(observation, result) < 0) {
            // we are now in older observations, give up
            break;
          }
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
          // In the event of simultaneous observations, get the highest pain level of those.
          if (result == null || (getPainScoreFromValue(observation) != null
              && getPainScoreFromValue(observation) < getPainScoreFromValue(result))) {
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
          // In the event of simultaneous observations, get the highest pain level of those.
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
    if (observation == null || Strings.isNullOrEmpty(
        ObservationUtils.getValueAsString(observation))) {
      return null;
    }

    int painScore = -1;
    try {
      painScore = Integer.parseInt(ObservationUtils.getValueAsString(observation));
    } catch (NumberFormatException ex) {
      log.warn("Non-numeric value:" + ObservationUtils.getValueAsString(observation)
          + " found in observation: " + observation.getId());
      return null;
    }
    if (painScore >= 0 && painScore <= 11) {
      return painScore;
    } else {
      log.warn("Unexpected quantiy of value:" + painScore + " found in observation: " + observation
          .getId().getValueAsString());
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

    return ObservationUtils.searchByTimeFrame(apiClient, encounterId, currentOrPriorShift).stream()
        .filter(observation -> codes.contains(ObservationUtils.getValueAsString(observation)))
        .collect(Collectors.toList());
  }
}
