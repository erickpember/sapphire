// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Checks if sub-glottic suction non-surgical airway is active for an encounter.
 */
public class SubglotticSuctionNonSurgicalAirway {

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to sub-glottic suction non-surgical airway.
   *
   * @param observation
   *     observation
   * @return true if observation is relevant to sub-glottic suction non-surgical airway
   */
  public static boolean isRelevant(Observation observation) {
    return ObservationCodeEnum.AIRWAY_DEVICE_CODE.getCode()
        .equals(observation.getCode().getCodingFirstRep().getCode());
  }

  /**
   * Checks if sub-glottic suction non-surgical airway is active for the encounter.
   *
   * @param encounterId
   *     encounter to search
   * @return true if sub-glottic suction non-surgical airway is active the encounter
   */
  public boolean test(String encounterId) {
    Observations observations = apiClient.getObservationClient().list(encounterId);

    Optional<Observation> freshestAirwayDevice = observations.findFreshest(
        ObservationCodeEnum.AIRWAY_DEVICE_CODE.getCode());

    if (!freshestAirwayDevice.isPresent()) {
      return false;
    }

    if (freshestAirwayDevice.get().getValue() != null) {

      String airwayName = ObservationUtils.airwayName(freshestAirwayDevice.get());

      if ("Endotracheal Tube - Subglottic".equals(
          ObservationUtils.getValueAsString(freshestAirwayDevice.get())) &&
          airwayName.contains("Non-Surgical Airway") &&
          !airwayName.contains("REMOVED")) {
        return true;
      }
    }

    return false;
  }
}
