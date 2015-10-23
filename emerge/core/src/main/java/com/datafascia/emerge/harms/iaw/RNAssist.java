// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.iaw;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import org.apache.commons.lang3.StringUtils;

/**
 * Utilities related to IAW RN assist.
 */
public class RNAssist {
  /**
   * Gets RN assist devices.
   *
   * @param observation
   *     observation to pull from
   * @return RN assist devices
   */
  public static String getAssistDevices(Observation observation) {
    String clinicianType = MobilityScore.getClinicianType(observation);
    if ("RN".equals(clinicianType)) {
      String[] identifierParts = observation.getValue().toString().split(":");
      if (identifierParts.length > 1) {
        return getBestRnAssistDevice(identifierParts[1]);
      }
    }

    return "Not Documented";
  }

  /**
   * Gets RN number of assists.
   *
   * @param observation
   *     observation to pull from
   * @return RN number of assists
   */
  public static String getNumberOfAssists(Observation observation) {
    String clinicianType = MobilityScore.getClinicianType(observation);
    if ("RN".equals(clinicianType)) {
      String[] identifierParts = observation.getValue().toString().split(":");
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
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Cane")) {
      return "Cane";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Crutches")) {
      return "Crutches";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Walker")) {
      return "Walker";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Gait Belt")) {
      return "Gait Belt";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Shower Chair")) {
      return "Shower Chair";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Toilet Riser")) {
      return "Toilet Riser";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Sit-to-stand Device (Non-powered)")) {
      return "Sit-to-stand Device (Non-powered)";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Sit-to-stand Device (Powered)")) {
      return "Sit-to-stand Device (Powered)";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Wheechair")) {
      return "Wheelchair";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Lateral Transfer Device")) {
      return "Lateral Transfer Device";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Neuro Chair")) {
      return "Neuro Chair";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Vertical dependent lift")) {
      return "Vertical dependent lift";
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, "Ceiling Lift")) {
      return "Ceiling Lift";
    }
    return "Other";
  }
}
