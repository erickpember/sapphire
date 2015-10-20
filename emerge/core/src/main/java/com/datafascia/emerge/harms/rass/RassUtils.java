// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.rass;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.base.Strings;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * RASS helper methods
 */
@Slf4j
public class RassUtils {

  // Private constructor disallows creating instances of this class.
  private RassUtils() {
  }

  /**
   * Given a list of observations, returns the observation with the lowest RASS level.
   *
   * @param observations
   *     observations to search
   * @return Lowest scoring observation found with the code. {@code null} if not found.
   */
  public static Observation lowestRassLevel(List<Observation> observations) {
    Observation result = null;

    for (Observation observation : observations) {
      if (observation.getCode().getCodingFirstRep().getCode()
          .equals(ObservationCodeEnum.RASS.getCode())) {
        if (getRassScoreFromValue(observation) != null && (result == null || getRassScoreFromValue(
            observation) < getRassScoreFromValue(result))) {
          result = observation;
        }
      }
    }

    return result;
  }

  /**
   * Given a list of observations, returns the observation with the highest RASS level.
   *
   * @param observations
   *     observations to search
   * @return Lowest scoring observation found with the code. {@code null} if not found.
   */
  public static Observation highestRassLevel(List<Observation> observations) {
    Observation result = null;

    for (Observation observation : observations) {
      if (observation.getCode().getCodingFirstRep().getCode()
          .equals(ObservationCodeEnum.RASS.getCode())) {
        if ((getRassScoreFromValue(observation) != null
            && getRassScoreFromValue(observation) != 11) && (result == null
            || getRassScoreFromValue(observation) > getRassScoreFromValue(result))) {
          result = observation;
        }
      }
    }

    return result;
  }

  /**
   * Attempts to pull the value of an Observation and parse it to a RASS score integer that is
   * between -5 and 11. If no such integer can be parsed, {@code null} is returned.
   *
   * @param observation
   *     An observation containing a RASS score in its value field.
   * @return The integer RASS score from the observation or  {@code null} if not found.
   */
  public static Integer getRassScoreFromValue(Observation observation) {
    if (observation == null || Strings.isNullOrEmpty(
        ObservationUtils.getValueAsString(observation))) {
      return null;
    }

    Integer rassScore = 11;
    try {
      rassScore = Integer.parseInt(ObservationUtils.getValueAsString(observation));
    } catch (NumberFormatException ex) {
      log.warn("Non-numeric rass score value:" + ObservationUtils.getValueAsString(observation)
          + " found in observation: " + observation.getId());
      return null;
    }
    if (rassScore >= -5 && rassScore <= 11) {
      return rassScore;
    } else {
      log.warn("Unexpected quantiy of rass score value:" + rassScore + " found in observation: "
          + observation.getId().getValueAsString());
      return null;
    }
  }
}
