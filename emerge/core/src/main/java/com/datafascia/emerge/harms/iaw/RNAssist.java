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
  public static AssistDeviceEnum getAssistDevices(Observation observation) {
    String[] identifierParts = observation.getValue().toString().split(":");
    if (identifierParts.length > 1) {
      return getBestRnAssistDevice(identifierParts[1]);
    }

    return AssistDeviceEnum.NOT_DOCUMENTED;
  }

  /**
   * Gets RN number of assists.
   *
   * @param observation
   *     observation to pull from
   * @return RN number of assists
   */
  public static NumberOfAssistsEnum getNumberOfAssists(Observation observation) {
    String[] identifierParts = observation.getValue().toString().split(":");
    if (identifierParts.length > 1) {
      return getBestRnNumberOfAssists(identifierParts[1]);
    }
    return NumberOfAssistsEnum.NOT_DOCUMENTED;
  }

  private static NumberOfAssistsEnum getBestRnNumberOfAssists(String assistDeviceString) {
    if (assistDeviceString.contains("none")) {
      return NumberOfAssistsEnum.NONE;
    }
    if (assistDeviceString.contains("1 person")) {
      return NumberOfAssistsEnum.ONE;
    }
    if (assistDeviceString.contains("2 persons")) {
      return NumberOfAssistsEnum.TWO;
    }
    if (assistDeviceString.contains("3 persons")) {
      return NumberOfAssistsEnum.THREE_PLUS;
    }
    return NumberOfAssistsEnum.NOT_DOCUMENTED;
  }

  /**
   * Compares a known number of assists to any corresponding values
   * that can be extracted from a provided observation.
   * Returns the highest valued device, if any.
   *
   * @param oldValue
   *     old value to compare
   * @param observation
   *     observation possibly containing a new value to compare
   * @return the highest value
   */
  public static NumberOfAssistsEnum getBestRnNumberOfAssists(NumberOfAssistsEnum oldValue,
      Observation observation) {
    NumberOfAssistsEnum newValue = getNumberOfAssists(observation);
    if (oldValue == NumberOfAssistsEnum.NONE || newValue == NumberOfAssistsEnum.NONE) {
      return NumberOfAssistsEnum.NONE;
    }
    if (oldValue == NumberOfAssistsEnum.ONE || newValue == NumberOfAssistsEnum.ONE) {
      return NumberOfAssistsEnum.ONE;
    }
    if (oldValue == NumberOfAssistsEnum.TWO || newValue == NumberOfAssistsEnum.TWO) {
      return NumberOfAssistsEnum.TWO;
    }
    if (oldValue == NumberOfAssistsEnum.THREE_PLUS || newValue == NumberOfAssistsEnum.THREE_PLUS) {
      return NumberOfAssistsEnum.THREE_PLUS;
    }
    return NumberOfAssistsEnum.NOT_DOCUMENTED;
  }

  private static AssistDeviceEnum getBestRnAssistDevice(String assistDeviceString) {
    if (assistDeviceString.contains(AssistDeviceEnum.NONE.getCode())) {
      return AssistDeviceEnum.NONE;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, AssistDeviceEnum.CANE.getCode())) {
      return AssistDeviceEnum.CANE;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, AssistDeviceEnum.CRUTCHES.getCode())) {
      return AssistDeviceEnum.CRUTCHES;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, AssistDeviceEnum.WALKER.getCode())) {
      return AssistDeviceEnum.WALKER;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, AssistDeviceEnum.GAIT_BELT.getCode())) {
      return AssistDeviceEnum.GAIT_BELT;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString,
        AssistDeviceEnum.SHOWER_CHAIR.getCode())) {
      return AssistDeviceEnum.SHOWER_CHAIR;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString,
        AssistDeviceEnum.TOILET_RISER.getCode())) {
      return AssistDeviceEnum.TOILET_RISER;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString,
        AssistDeviceEnum.SIT_TO_STAND_DEVICE_NON_POWERED.getCode())) {
      return AssistDeviceEnum.SIT_TO_STAND_DEVICE_NON_POWERED;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString,
        AssistDeviceEnum.SIT_TO_STAND_DEVICE_POWERED.getCode())) {
      return AssistDeviceEnum.SIT_TO_STAND_DEVICE_POWERED;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString,
        AssistDeviceEnum.WHEELCHAIR.getCode())) {
      return AssistDeviceEnum.WHEELCHAIR;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, AssistDeviceEnum.LATERAL_TRANSFER_DEVICE
        .getCode())) {
      return AssistDeviceEnum.LATERAL_TRANSFER_DEVICE;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString,
        AssistDeviceEnum.NEURO_CHAIR.getCode())) {
      return AssistDeviceEnum.NEURO_CHAIR;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString, AssistDeviceEnum.VERTICAL_DEPENDENT_LIFT
        .getCode())) {
      return AssistDeviceEnum.VERTICAL_DEPENDENT_LIFT;
    }
    if (StringUtils.containsIgnoreCase(assistDeviceString,
        AssistDeviceEnum.CEILING_LIFT.getCode())) {
      return AssistDeviceEnum.CEILING_LIFT;
    }
    return AssistDeviceEnum.OTHER;
  }

  /**
   * Compares a known assist device to any devices that can be extracted
   * from a provided observation.
   * Returns the highest valued device, if any.
   *
   * @param assistDevice
   *     device to compare
   * @param observation
   *     observation containing more devices to compare
   * @return the highest valued device
   */
  public static AssistDeviceEnum getBestRnAssistDevice(AssistDeviceEnum assistDevice,
      Observation observation) {
    if (assistDevice == null) {
      return getAssistDevices(observation);
    } else {
      return getBestRnAssistDevice(getAssistDevices(observation).getCode() +
          assistDevice.getCode());
    }
  }
}
