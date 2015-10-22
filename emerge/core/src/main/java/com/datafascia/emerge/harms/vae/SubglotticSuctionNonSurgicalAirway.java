// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import java.util.List;

/**
 * Utilities for Sub-Glottic Suction ETT
 */
public class SubglotticSuctionNonSurgicalAirway {
  /**
   * Determines whether a subglottic suction ETT is active for a given encounter.
   *
   * @param client The client to use.
   * @param encounterId The encounter to query.
   * @return Whether a subglottic suction ETT is active for the given encounter.
   */
  public static boolean subglotticSuctionEtt(ClientBuilder client, String encounterId) {
    List<Observation> airwayDevices = client.getObservationClient().searchObservation(encounterId,
        "304894155", null);

    Observation freshestAirwayDevice = ObservationUtils.findFreshestObservation(airwayDevices);
    String airway = ObservationUtils.airwayName(freshestAirwayDevice);

    String airwaySubglotticSuctionCapabilityStatus = null;
    if (airway.equals("Surgical Airway")) {
      airwaySubglotticSuctionCapabilityStatus = "Unknown";
    } else if (freshestAirwayDevice.getValue()
        .toString().equals("Endotracheal Tube – Subglottic")) {
      airwaySubglotticSuctionCapabilityStatus = "Present";
    } else if (!freshestAirwayDevice.getValue()
        .toString().equals("Endotracheal Tube – Subglottic")) {
      airwaySubglotticSuctionCapabilityStatus = "Absent";
    }

    if (airway.equals("Non-Surgical Airway")
        && airwaySubglotticSuctionCapabilityStatus.equals("Present")) {
      return true;
    }

    return false;
  }
}
