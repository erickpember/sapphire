// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.EncounterUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;

import static com.datafascia.api.client.Observations.isEffectiveAfter;

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

  private static boolean valueEquals(Optional<Observation> observation, String value) {
    return observation.get().getValue().toString().equals(value);
  }

  /**
   * Checks if the patient is ventilated.
   *
   * @param encounter
   *     encounter to search
   * @return true if the conditions that indicate ventilation are met
   */
  public boolean isVentilated(Encounter encounter) {
    Instant icuAdmitTime = EncounterUtils.getIcuPeriodStart(encounter);
    Observations observations = apiClient.getObservationClient()
        .list(encounter.getId().getIdPart());

    List<Observation> invasiveVentStatus = new ArrayList<>();

    observations.findFreshest(ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(), icuAdmitTime,
        null)
        .ifPresent(observation -> invasiveVentStatus.add(observation));

    observations.findFreshest(ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode())
        .ifPresent(observation -> invasiveVentStatus.add(observation));

    Optional<Observation> freshestInvasiveVentStatus = invasiveVentStatus.stream()
        .max(Observations.EFFECTIVE_COMPARATOR);

    Optional<Observation> freshestIntubation = observations.findFreshest(
        ObservationCodeEnum.INTUBATION.getCode(), "Yes", icuAdmitTime, null);

    Optional<Observation> freshestExtubation = observations.findFreshest(
        ObservationCodeEnum.EXTUBATION.getCode(), "Yes");

    Optional<Observation> freshestETTInvasiveVentInitiation = observations.findFreshest(
        ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode());

    Optional<Observation> freshestETTOngoingInvasiveVent = observations.findFreshest(
        ObservationCodeEnum.ETT_ONGOING_INVASIVE_VENT.getCode());

    Optional<Observation> freshestTrachInvasiveVentInitiation = observations.findFreshest(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_INITIATION.getCode());

    Optional<Observation> freshestTrachOngoingInvasiveVent = observations.findFreshest(
        ObservationCodeEnum.TRACH_ONGOING_INVASIVE_VENT.getCode(), "Yes");

    if (freshestInvasiveVentStatus.isPresent()) {
      String value = freshestInvasiveVentStatus.get().getValue().toString();
      if ((value.equals("Continue") || value.equals("Patient back on Invasive")) &&
          (!freshestExtubation.isPresent() ||
           isEffectiveAfter(freshestInvasiveVentStatus, freshestExtubation))) {
        return true;
      }
    }

    if (freshestIntubation.isPresent() &&
        (!freshestExtubation.isPresent() ||
         !isEffectiveAfter(freshestExtubation, freshestIntubation)) &&
        (!freshestInvasiveVentStatus.isPresent() ||
         !valueEquals(freshestInvasiveVentStatus, "Discontinue") ||
         !valueEquals(freshestInvasiveVentStatus, "Patient Taken Off") ||
         !isEffectiveAfter(freshestInvasiveVentStatus, freshestIntubation))) {
      return true;
    }

    if (freshestInvasiveVentStatus.isPresent() &&
        (valueEquals(freshestInvasiveVentStatus, "Discontinue") ||
         valueEquals(freshestInvasiveVentStatus, "Patient Taken Off")) &&
        (freshestIntubation.isPresent() &&
         isEffectiveAfter(freshestIntubation, freshestInvasiveVentStatus)) ||
        (freshestETTInvasiveVentInitiation.isPresent() && freshestInvasiveVentStatus.isPresent() &&
         isEffectiveAfter(freshestETTInvasiveVentInitiation, freshestInvasiveVentStatus)) ||
        (freshestETTOngoingInvasiveVent.isPresent() && freshestInvasiveVentStatus.isPresent() &&
         isEffectiveAfter(freshestETTOngoingInvasiveVent, freshestInvasiveVentStatus)) ||
        (freshestTrachInvasiveVentInitiation.isPresent() &&
         freshestInvasiveVentStatus.isPresent() &&
         isEffectiveAfter(freshestTrachInvasiveVentInitiation, freshestInvasiveVentStatus)) ||
        (freshestTrachOngoingInvasiveVent.isPresent() && freshestInvasiveVentStatus.isPresent() &&
         isEffectiveAfter(freshestTrachOngoingInvasiveVent, freshestInvasiveVentStatus))) {
      return true;
    }
    return false;
  }
}
