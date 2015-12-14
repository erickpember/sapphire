// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.iaw;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Utilities related to IAW mobility score.
 */
public class MobilityScore {

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to mobility score.
   *
   * @param observation
   *     observation
   * @return true if observation is relevant to mobility score
   */
  public static boolean isRelevant(Observation observation) {
    return ObservationCodeEnum.MOBILITY_SCORE.getCode().equals(
        observation.getCode().getCodingFirstRep().getCode());
  }

  /**
   * Gets the freshest mobility observation.
   *
   * @param encounterId
   *     encounter to search
   * @return optional observation, empty if not found
   */
  public Optional<Observation> getFreshestObservation(String encounterId) {
    List<Observation> observations = apiClient.getObservationClient().searchObservation(
        encounterId, ObservationCodeEnum.MOBILITY_SCORE.getCode(), null);
    if (observations.isEmpty()) {
      return Optional.empty();
    }

    return Optional.of(ObservationUtils.findFreshestObservation(observations));
  }

  /**
   * Gets mobility score time.
   *
   * @param observation
   *     observation to pull from
   * @return effective time of freshest mobility event
   */
  public static Date getMobilityScoreTime(Observation observation) {
    return ObservationUtils.getEffectiveDate(observation);
  }

  /**
   * Gets mobility level achieved.
   *
   * @param observation
   *     observation to pull from
   * @return level achieved of the freshest mobility event
   */
  public static int getMobilityLevelAchieved(Observation observation) {
    String[] typeScoreParts = getTypeScoreParts(observation);

    if (typeScoreParts != null) {
      if (typeScoreParts.length > 1) {
        return Integer.parseInt(typeScoreParts[1]);
      }
    }

    return 0;
  }

  /**
   * Gets mobility clinician type.
   *
   * @param observation
   *     observation to pull from
   * @return clinician type of the freshest mobility event
   */
  public static String getClinicianType(Observation observation) {
    String[] typeScoreParts = getTypeScoreParts(observation);

    if (typeScoreParts != null) {
      if (typeScoreParts.length > 0) {
        return typeScoreParts[0];
      }
    }

    return null;
  }

  private static String[] getTypeScoreParts(Observation observation) {
    String[] identifierParts = observation.getValue().toString().split(":");
    if (identifierParts.length > 0) {
      String[] typeScoreParts = identifierParts[0].split("_");
      return typeScoreParts;
    }
    return null;
  }
}
