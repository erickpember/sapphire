// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import java.util.List;

/**
 * Tools for tracheostomy.
 */
public class Tracheostomy {
  /**
   * Determines whether a tracheostomy is active for a given encounter.
   *
   * @param client The client to use.
   * @param encounterId The encounter to query.
   * @return Whether a tracheostomy is active for the given encounter.
   */
  public static boolean tracheostomy(ClientBuilder client, String encounterId) {
    List<Observation> airwayDevices = client.getObservationClient().searchObservation(encounterId,
        "304894155", null);

    Observation freshestAirwayDevice = ObservationUtils.findFreshestObservation(airwayDevices);
    String airway = airwayName(freshestAirwayDevice);

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

    boolean tracheostomy = false;
    if (airway.equals("Surgical Airway") && airwaySubglotticSuctionCapabilityStatus != null
        && airwaySubglotticSuctionCapabilityStatus.equals("Present")) {
      tracheostomy = true;
    }
    return tracheostomy;
  }

  private static String airwayName(Observation observation) {
    String[] identifierParts = observation.getIdentifierFirstRep().getValue().split("^");
    if (identifierParts.length > 1) {
      String[] propertyParts = identifierParts[2].split("-");

      if (propertyParts.length > 1) {
        return propertyParts[2];
      }
    }

    return null;
  }
}
