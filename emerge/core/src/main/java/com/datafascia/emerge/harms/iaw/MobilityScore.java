// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.iaw;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import java.util.Date;
import java.util.List;

/**
 * Utilities related to IAW mobility score.
 */
public class MobilityScore {
  /**
   * Implements mobility score time.
   *
   * @param client API client.
   * @param encounterId Relevant encounter ID.
   * @return The effect date of the last mobility event.
   */
  public static Date mobilityScoreTime(ClientBuilder client, String encounterId) {
    return ObservationUtils.getEffectiveDate(freshestObservation(client, encounterId));
  }

  /**
   * Implements mobility level achieved.
   *
   * @param observation The observation to pull from.
   * @return The level achieved of the last mobility event.
   */
  public static int mobilityLevelAchieved(Observation observation) {
    String[] typeScoreParts = getTypeScoreParts(observation);

    if (typeScoreParts != null) {
      if (typeScoreParts.length > 1) {
        return Integer.parseInt(typeScoreParts[1]);
      }
    }

    return 0;
  }

  /**
   * Implements mobility clinician type.
   *
   * @param observation The observation to pull from.
   * @return The clinician type of the last mobility event.
   */
  public static String clinicianType(Observation observation) {
    String[] typeScoreParts = getTypeScoreParts(observation);

    if (typeScoreParts != null) {
      if (typeScoreParts.length > 0) {
        return typeScoreParts[0];
      }
    }

    return null;
  }

  private static String[] getTypeScoreParts(Observation observation) {
    String[] identifierParts = observation.getIdentifierFirstRep().getValue().split(":");
    if (identifierParts.length > 0) {
      String[] typeScoreParts = identifierParts[0].split("_");
      return typeScoreParts;
    }
    return null;
  }

  /**
   * Returns the freshest mobility observation.
   *
   * @param client API client.
   * @param encounterId Relevant encounter ID.
   * @return The last mobility event.
   */
  public static Observation freshestObservation(ClientBuilder client, String encounterId) {
    List<Observation> observations = client.getObservationClient().searchObservation(encounterId,
        "30489003", null);
    return ObservationUtils.findFreshestObservation(observations);
  }
}
