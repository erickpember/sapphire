// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.iaw;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;

/**
 * Utilities related to IAW RN assist.
 */
public class RNAssist {
  /**
   * RN Assist Devices.
   *
   * @param client The client to use.
   * @param encounterId The encounter to query.
   * @return RN assist devices.
   */
  public static String assistDevices(ClientBuilder client, String encounterId) {
    Observation observation = MobilityScore.freshestObservation(client, encounterId);
    String clinicianType = MobilityScore.clinicianType(observation);

    if (clinicianType != null && clinicianType.equals("RN")) {
      String[] identifierParts = observation.getIdentifierFirstRep().getValue().split(":");
      if (identifierParts.length > 1) {
        return getBestRnAssistDevice(identifierParts[1]);
      }
    }
    return "Not Documented";
  }

  /**
   * RN Number of Assists.
   *
   * @param client The client to use.
   * @param encounterId The encounter to query.
   * @return RN number of assists.
   */
  public static String numberOfAssists(ClientBuilder client, String encounterId) {
    Observation observation = MobilityScore.freshestObservation(client, encounterId);
    String clinicianType = MobilityScore.clinicianType(observation);

    if (clinicianType != null && clinicianType.equals("RN")) {
      String[] identifierParts = observation.getIdentifierFirstRep().getValue().split(":");
      if (identifierParts.length > 1) {
        return getBestRnNumberOfAssists(identifierParts[1]);
      }
    }
    return "Not Documented";
  }

  private static String getBestRnNumberOfAssists(String assistDeviceString) {
    if (assistDeviceString.contains("none")) {
      return "None";
    }
    if (assistDeviceString.contains("1 person")) {
      return "1";
    }
    if (assistDeviceString.contains("2 persons")) {
      return "2";
    }
    if (assistDeviceString.contains("3 persons")) {
      return "3+";
    }
    return "Not Documented";
  }

  private static String getBestRnAssistDevice(String assistDeviceString) {
    if (assistDeviceString.contains("None")) {
      return "None";
    }
    if (assistDeviceString.contains("Cane")) {
      return "Cane";
    }
    if (assistDeviceString.contains("Crutches")) {
      return "Crutches";
    }
    if (assistDeviceString.contains("Walker")) {
      return "Walker";
    }
    if (assistDeviceString.contains("Gait Belt")) {
      return "Gait Belt";
    }
    if (assistDeviceString.contains("Shower Chair")) {
      return "Shower Chair";
    }
    if (assistDeviceString.contains("Toilet Riser")) {
      return "Toilet Riser";
    }
    if (assistDeviceString.contains("Sit-to-stand Device (Non-powered)")) {
      return "Sit-to-stand Device (Non-powered)";
    }
    if (assistDeviceString.contains("Sit-to-stand Device (Powered)")) {
      return "Sit-to-stand Device (Powered)";
    }
    if (assistDeviceString.contains("Wheechair")) {
      return "Wheelchair";
    }
    if (assistDeviceString.contains("Lateral Transfer Device")) {
      return "Lateral Transfer Device";
    }
    if (assistDeviceString.contains("Neuro Chair")) {
      return "Neuro Chair";
    }
    if (assistDeviceString.contains("Vertical dependent lift")) {
      return "Vertical dependent lift";
    }
    if (assistDeviceString.contains("Ceiling Lift")) {
      return "Ceiling Lift";
    }
    return "Other";
  }
}
