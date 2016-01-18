// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.TimestampedMaybe;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Checks if sub-glottic suction non-surgical airway is active for an encounter.
 */
public class SubglotticSuctionNonSurgicalAirway {

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if the airway has been discontinued
   *
   * @param observations
   *     observations for the encounter
   * @param encounterId
   *     airway ID to search
   * @return true if the airway is active
   */
  private boolean airwayIsActive(Observations observations, String airwayId) {
    List<Observation> removedDateObservations =
        observations.list(ObservationCodeEnum.LINE_REMOVAL_DATE.getCode(), null, null);
    for (Observation observation : removedDateObservations) {
      String observationAirwayId = observation.getIdentifierFirstRep().getValue();
      if (airwayId.equals(observationAirwayId)) {
        return false;
      }
    }

    List<Observation> removedTimeObservations =
        observations.list(ObservationCodeEnum.LINE_REMOVAL_TIME.getCode(), null, null);
    for (Observation observation : removedTimeObservations) {
      String observationAirwayId = observation.getIdentifierFirstRep().getValue();
      if (airwayId.equals(observationAirwayId)) {
        return false;
      }
    }

    return true;
  }

  /**
   * Checks if observation is relevant to sub-glottic suction non-surgical airway.
   *
   * @param observation
   *     observation
   * @return true if observation is relevant to sub-glottic suction non-surgical airway
   */
  public static boolean isRelevant(Observation observation) {
    return ObservationCodeEnum.AIRWAY_DEVICE_CODE.getCode()
            .equals(observation.getCode().getCodingFirstRep().getCode()) ||
        ObservationCodeEnum.LINE_REMOVAL_DATE.getCode()
            .equals(observation.getCode().getCodingFirstRep().getCode()) ||
        ObservationCodeEnum.LINE_REMOVAL_TIME.getCode()
            .equals(observation.getCode().getCodingFirstRep().getCode());
  }

  /**
   * Checks if sub-glottic suction non-surgical airway is active for the encounter.
   *
   * @param encounterId
   *     encounter to search
   * @return true if sub-glottic suction non-surgical airway is active the encounter
   */
  public TimestampedMaybe.Value test(String encounterId) {
    Observations observations = apiClient.getObservationClient().list(encounterId);

    Optional<Observation> freshestAirwayDevice = observations.findFreshest(
        ObservationCodeEnum.AIRWAY_DEVICE_CODE.getCode());

    if (!freshestAirwayDevice.isPresent()) {
      return TimestampedMaybe.Value.NOT_DOCUMENTED;
    }

    if (freshestAirwayDevice.get().getValue() != null) {

      String airwayName = ObservationUtils.airwayName(freshestAirwayDevice.get());

      if ("Endotracheal Tube - Subglottic".equals(
          ObservationUtils.getValueAsString(freshestAirwayDevice.get())) &&
          airwayName.contains("Non-Surgical Airway") &&
          airwayIsActive(observations,
              freshestAirwayDevice.get().getIdentifierFirstRep().getValue())) {
        return TimestampedMaybe.Value.YES;
      }
    }

    return TimestampedMaybe.Value.NO;
  }
}
