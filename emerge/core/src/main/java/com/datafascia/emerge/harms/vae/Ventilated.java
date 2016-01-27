// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.EncounterUtils;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Instant;
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

  /**
   * Checks if the patient is ventilated.
   *
   * @param encounter
   *     encounter to search
   * @return true if the conditions that indicate ventilation are met
   */
  public boolean isVentilated(Encounter encounter) {
    Instant icuAdmitTime = EncounterUtils.getIcuPeriodStart(encounter);
    Observations observations =
        apiClient.getObservationClient().list(encounter.getId().getIdPart());
    return isVentilated(observations, icuAdmitTime);
  }

  /**
   * Checks if the patient is ventilated.
   *
   * @param observations
   *     Observations for the encounter.
   * @param icuAdmitTime
   *     ICU admit time.
   * @return true if the conditions that indicate ventilation are met
   */
  public boolean isVentilated(Observations observations, Instant icuAdmitTime) {
    Optional<Observation> freshestEttInvasiveVentStatus = observations.findFreshest(
        ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(), icuAdmitTime,
        null);

    Optional<Observation> freshestTrachInvasiveVentStatus = observations.findFreshest(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode(), icuAdmitTime,
        null);

    Optional<Observation> freshestETTInvasiveVentInitiation = observations.findFreshest(
        ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode(), icuAdmitTime,
        null);

    Optional<Observation> freshestETTOngoingInvasiveVent = observations.findFreshest(
        ObservationCodeEnum.ETT_ONGOING_INVASIVE_VENT.getCode(), icuAdmitTime,
        null);

    Optional<Observation> freshestTrachInvasiveVentInitiation = observations.findFreshest(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_INITIATION.getCode(), icuAdmitTime,
        null);

    Optional<Observation> freshestTrachOngoingInvasiveVent = observations.findFreshest(
        ObservationCodeEnum.TRACH_ONGOING_INVASIVE_VENT.getCode(), icuAdmitTime,
        null);

    if (freshestEttInvasiveVentStatus.isPresent()) {
      if (isContinueOrBackOnInvasive(freshestEttInvasiveVentStatus.get())) {
        if (freshestTrachInvasiveVentStatus.isPresent()
            && isEffectiveAfter(freshestTrachInvasiveVentStatus, freshestEttInvasiveVentStatus)
            && isDiscontinueOrPatientTakenOff(freshestTrachInvasiveVentStatus.get())) {
          return false;
        } else {
          return true;
        }
      }

      if (isDiscontinueOrPatientTakenOff(freshestEttInvasiveVentStatus.get())) {
        if (freshestETTInvasiveVentInitiation.isPresent()
            && isEffectiveAfter(freshestETTInvasiveVentInitiation, freshestEttInvasiveVentStatus)
            && isYes(freshestETTInvasiveVentInitiation.get())) {
          return true;
        } else if (freshestETTOngoingInvasiveVent.isPresent()
            && isEffectiveAfter(freshestETTOngoingInvasiveVent, freshestEttInvasiveVentStatus)
            && isYes(freshestETTOngoingInvasiveVent.get())) {
          return true;
        } else if (freshestTrachInvasiveVentInitiation.isPresent()
            && isEffectiveAfter(freshestTrachInvasiveVentInitiation, freshestEttInvasiveVentStatus)
            && isYes(freshestTrachInvasiveVentInitiation.get())) {
          return true;
        } else if (freshestTrachOngoingInvasiveVent.isPresent()
            && isEffectiveAfter(freshestTrachOngoingInvasiveVent, freshestEttInvasiveVentStatus)
            && isYes(freshestTrachOngoingInvasiveVent.get())) {
          return true;
        }
      }
    }

    if (freshestTrachInvasiveVentStatus.isPresent()) {
      if (isContinueOrBackOnInvasive(freshestTrachInvasiveVentStatus.get())) {
        if (freshestEttInvasiveVentStatus.isPresent() && isEffectiveAfter(
            freshestEttInvasiveVentStatus, freshestTrachInvasiveVentStatus)
            && isDiscontinueOrPatientTakenOff(freshestEttInvasiveVentStatus.get())) {
          return false;
        } else {
          return true;
        }
      }

      if (isDiscontinueOrPatientTakenOff(freshestTrachInvasiveVentStatus.get())) {
        if (freshestETTInvasiveVentInitiation.isPresent()
            && isEffectiveAfter(freshestETTInvasiveVentInitiation, freshestTrachInvasiveVentStatus)
            && isYes(freshestETTInvasiveVentInitiation.get())) {
          return true;
        } else if (freshestETTOngoingInvasiveVent.isPresent()
            && isEffectiveAfter(freshestETTOngoingInvasiveVent, freshestTrachInvasiveVentStatus)
            && isYes(freshestETTOngoingInvasiveVent.get())) {
          return true;
        } else if (freshestTrachInvasiveVentInitiation.isPresent()
            && isEffectiveAfter(freshestTrachInvasiveVentInitiation,
                freshestTrachInvasiveVentStatus)
            && isYes(freshestTrachInvasiveVentInitiation.get())) {
          return true;
        } else if (freshestTrachOngoingInvasiveVent.isPresent()
            && isEffectiveAfter(freshestTrachOngoingInvasiveVent, freshestTrachInvasiveVentStatus)
            && isYes(freshestTrachOngoingInvasiveVent.get())) {
          return true;
        }
      }
    }

    return false;
  }

  private boolean isContinueOrBackOnInvasive(Observation obs) {
    String value = ObservationUtils.getValueAsString(obs);
    return ("Continue".equals(value) || "Patient back on Invasive".equals(value));
  }

  private boolean isDiscontinueOrPatientTakenOff(Observation obs) {
    String value = ObservationUtils.getValueAsString(obs);
    return ("Discontinue".equals(value) || "Patient taken off".equals(value));
  }

  private boolean isYes(Observation obs) {
    String value = ObservationUtils.getValueAsString(obs);
    return ("Yes".equals(value));
  }
}
