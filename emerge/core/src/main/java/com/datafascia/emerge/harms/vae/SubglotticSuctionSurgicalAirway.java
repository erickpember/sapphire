// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import java.util.List;
import javax.inject.Inject;

/**
 * Checks if subglottic suction surgical airway (also known as tracheostomy)
 * is active for an encounter.
 */
public class SubglotticSuctionSurgicalAirway {

  private static final String AIRWAY_DEVICE_CODE = "304894155";

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to subglottic suction surgical airway.
   *
   * @param observation
   *     observation
   * @return true if observation is relevant to subglottic suction surgical airway
   */
  public static boolean isRelevant(Observation observation) {
    return AIRWAY_DEVICE_CODE.equals(observation.getCode().getCodingFirstRep().getCode());
  }

  /**
   * Checks if subglottic suction surgical airway is active for the encounter.
   *
   * @param encounterId
   *     encounter to search
   * @return true if subglottic suction surgical airway is active the encounter
    */
  public boolean test(String encounterId) {
    List<Observation> airwayDevices = apiClient.getObservationClient().searchObservation(
        encounterId, AIRWAY_DEVICE_CODE, null);
    if (airwayDevices.isEmpty()) {
      return false;
    }

    Observation freshestAirwayDevice = ObservationUtils.findFreshestObservation(airwayDevices);
    String airway = ObservationUtils.airwayName(freshestAirwayDevice);

    String airwaySubglotticSuctionCapabilityStatus = null;
    if ("Surgical Airway".equals(airway)) {
      airwaySubglotticSuctionCapabilityStatus = "Unknown";
    } else if (freshestAirwayDevice.getValue()
        .toString().equals("Endotracheal Tube – Subglottic")) {
      airwaySubglotticSuctionCapabilityStatus = "Present";
    } else if (!freshestAirwayDevice.getValue()
        .toString().equals("Endotracheal Tube – Subglottic")) {
      airwaySubglotticSuctionCapabilityStatus = "Absent";
    }

    if ("Surgical Airway".equals(airway)
        && "Present".equals(airwaySubglotticSuctionCapabilityStatus)) {
      return true;
    }

    return false;
  }
}
