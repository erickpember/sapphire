// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationEffectiveComparator;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

/**
 * Implements VAE Ventilated
 */
public class Ventilated {

  private static final Set<String> RELEVANT_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode(),
      ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(),
      ObservationCodeEnum.ETT_ONGOING_INVASIVE_VENT.getCode(),
      ObservationCodeEnum.TRACH_INVASIVE_VENT_INITIATION.getCode(),
      ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode(),
      ObservationCodeEnum.TRACH_ONGOING_INVASIVE_VENT.getCode(),
      ObservationCodeEnum.INTUBATION.getCode(),
      ObservationCodeEnum.EXTUBATION.getCode());

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to ventilated.
   *
   * @param observation
   *     observation
   * @return true if observation is relevant to ventilated
   */
  public static boolean isRelevant(Observation observation) {
    return RELEVANT_OBSERVATION_CODES.contains(observation.getCode().getCodingFirstRep().getCode());
  }

  /**
   * Checks if ventilated.
   *
   * @param encounterId
   *     encounter to search
   * @return true if the conditions that indicate ventilation are met
   */
  public boolean isVentilated(String encounterId) {
    List<Observation> invasiveVentStatus = new ArrayList<>();

    Observation freshestETTInvasiveVentStatus = ObservationUtils.findFreshestObservationForCode(
        apiClient, encounterId, ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode());
    if (freshestETTInvasiveVentStatus != null) {
      invasiveVentStatus.add(freshestETTInvasiveVentStatus);
    }

    Observation freshestTrachInvasiveVentStatus = ObservationUtils.findFreshestObservationForCode(
        apiClient, encounterId, ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode());
    if (freshestTrachInvasiveVentStatus != null) {
      invasiveVentStatus.add(freshestTrachInvasiveVentStatus);
    }

    Observation freshestInvasiveVentStatus = ObservationUtils.findFreshestObservation(
        invasiveVentStatus);

    Observation freshestIntubation = ObservationUtils.findFreshestObservationForCodeAndValue(
        apiClient, encounterId, ObservationCodeEnum.INTUBATION.getCode(), "Yes");

    Observation freshestExtubation = ObservationUtils.findFreshestObservationForCodeAndValue(
        apiClient, encounterId, ObservationCodeEnum.EXTUBATION.getCode(), "Yes");

    Observation freshestETTInvasiveVentInitiation = ObservationUtils.findFreshestObservationForCode(
        apiClient, encounterId, ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode());

    Observation freshestETTOngoingInvasiveVent = ObservationUtils.findFreshestObservationForCode(
        apiClient, encounterId, ObservationCodeEnum.ETT_ONGOING_INVASIVE_VENT.getCode());

    Observation freshestTrachInvasiveVentInitiation = ObservationUtils.
        findFreshestObservationForCode(
            apiClient, encounterId, ObservationCodeEnum.TRACH_INVASIVE_VENT_INITIATION.getCode());

    Observation freshestTrachOngoingInvasiveVent = ObservationUtils.
        findFreshestObservationForCodeAndValue(apiClient, encounterId,
            ObservationCodeEnum.TRACH_ONGOING_INVASIVE_VENT.getCode(), "Yes");

    ObservationEffectiveComparator comparator = new ObservationEffectiveComparator();

    if (freshestInvasiveVentStatus != null) {
      String value = freshestInvasiveVentStatus.getValue().toString();
      if ((value.equals("Continue") || value.equals("Patient back on Invasive"))
          && (freshestExtubation == null
          || comparator.compare(freshestExtubation, freshestInvasiveVentStatus) < 0)) {
        return true;
      }
    }

    if (freshestIntubation != null && (freshestExtubation == null || comparator.compare(
        freshestExtubation, freshestIntubation) <= 0) && (freshestInvasiveVentStatus == null
        || !freshestInvasiveVentStatus.getValue().toString().equals("Discontinue")
        || !freshestInvasiveVentStatus.getValue().toString().equals("Patient Taken Off")
        || comparator.compare(freshestInvasiveVentStatus, freshestIntubation) <= 0)) {
      return true;
    }

    if (freshestInvasiveVentStatus != null
        && (freshestInvasiveVentStatus.getValue().toString().equals("Discontinue")
        || freshestInvasiveVentStatus.getValue().toString().equals("Patient Taken Off"))
        && (freshestIntubation != null && comparator.
        compare(freshestIntubation, freshestInvasiveVentStatus) > 0)
        || (freshestETTInvasiveVentInitiation != null && comparator.compare(
            freshestETTInvasiveVentInitiation, freshestInvasiveVentStatus) > 0)
        || (freshestETTOngoingInvasiveVent != null && comparator.
        compare(
            freshestETTOngoingInvasiveVent, freshestInvasiveVentStatus) > 0)
        || (freshestTrachInvasiveVentInitiation != null && comparator.compare(
            freshestTrachInvasiveVentInitiation, freshestInvasiveVentStatus) > 0)
        || (freshestTrachOngoingInvasiveVent != null && comparator.
        compare(
            freshestTrachOngoingInvasiveVent, freshestInvasiveVentStatus) > 0)) {
      return true;
    }
    return false;
  }
}
