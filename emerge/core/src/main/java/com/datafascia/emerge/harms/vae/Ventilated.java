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
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;

import static com.datafascia.api.client.Observations.isEffectiveAfter;

/**
 * Implements VAE Ventilated
 */
public class Ventilated {

  private static final Set<String> INVASIVE_VENT_STATUS_CODES = ImmutableSet.of(
      ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(),
      ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode());

  private static final Set<String> INIT_OR_ONGOING_VENT_CODES = ImmutableSet.of(
      ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode(),
      ObservationCodeEnum.ETT_ONGOING_INVASIVE_VENT.getCode(),
      ObservationCodeEnum.TRACH_INVASIVE_VENT_INITIATION.getCode(),
      ObservationCodeEnum.TRACH_ONGOING_INVASIVE_VENT.getCode());

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
  private Clock clock;

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
    Instant now = Instant.now(clock);
    Observations observations =
        apiClient.getObservationClient().list(encounter.getId().getIdPart());
    return isVentilated(observations, icuAdmitTime, now);
  }

  /**
   * Checks if the patient is ventilated.
   *
   * @param observations
   *     Observations for the encounter.
   * @param icuAdmitTime
   *     ICU admit time.
   * @param now
   *     the current time
   * @return true if the conditions that indicate ventilation are met
   */
  public boolean isVentilated(Observations observations, Instant icuAdmitTime, Instant now) {
    // Get the specific observations within the past 12 hours
    Instant twelveHoursAgo = now.minus(12, ChronoUnit.HOURS);

    Optional<Observation> twelveHourTidalVolume = observations.findFreshest(
        ObservationCodeEnum.TIDAL_VOLUME.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourBreathType = observations.findFreshest(
        ObservationCodeEnum.BREATH_TYPE.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourFio2 = observations.findFreshest(
        ObservationCodeEnum.FIO2.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourPeep = observations.findFreshest(
        ObservationCodeEnum.PEEP.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourVentMode = observations.findFreshest(
        ObservationCodeEnum.VENT_MODE.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourPressureSupport = observations.findFreshest(
        ObservationCodeEnum.PRESSURE_SUPPORT.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourEttInvasiveVentInitiation = observations.findFreshest(
        ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourEttOngoingInvasiveVent = observations.findFreshest(
        ObservationCodeEnum.ETT_ONGOING_INVASIVE_VENT.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourEttInvasiveVentStatus = observations.findFreshest(
        ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourTrachInvasiveVentInitiation = observations.findFreshest(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_INITIATION.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourTrachOngoingInvasiveVent = observations.findFreshest(
        ObservationCodeEnum.TRACH_ONGOING_INVASIVE_VENT.getCode(), twelveHoursAgo, null);
    Optional<Observation> twelveHourTrachInvasiveVentStatus = observations.findFreshest(
        ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode(), twelveHoursAgo, null);

    if (!twelveHourTidalVolume.isPresent() && !twelveHourBreathType.isPresent() &&
        !twelveHourFio2.isPresent() && !twelveHourPeep.isPresent() &&
        !twelveHourVentMode.isPresent() && !twelveHourPressureSupport.isPresent() &&
        !twelveHourEttInvasiveVentInitiation.isPresent() &&
        !twelveHourEttOngoingInvasiveVent.isPresent() &&
        !twelveHourEttInvasiveVentStatus.isPresent() &&
        !twelveHourTrachInvasiveVentInitiation.isPresent() &&
        !twelveHourTrachOngoingInvasiveVent.isPresent() &&
        !twelveHourTrachInvasiveVentStatus.isPresent()) {
      return false;
    }

    Optional<Observation> freshestInvasiveVentStatus = observations.findFreshest(
        INVASIVE_VENT_STATUS_CODES, icuAdmitTime, null);
    Optional<Observation> freshestInitOrOngoingVent = observations.findFreshest(
        INIT_OR_ONGOING_VENT_CODES, icuAdmitTime, null);

    if (freshestInvasiveVentStatus.isPresent()) {
      if (isContinueOrBackOnInvasive(freshestInvasiveVentStatus.get())) {
        return true;
      } else if (isDiscontinueOrPatientTakenOff(freshestInvasiveVentStatus.get())) {
        if (freshestInitOrOngoingVent.isPresent()
            && isYes(freshestInitOrOngoingVent.get())
            && isEffectiveAfter(freshestInitOrOngoingVent, freshestInvasiveVentStatus)) {
          return true;
        }
      }
    } else {
      if (freshestInitOrOngoingVent.isPresent()
          && isYes(freshestInitOrOngoingVent.get())) {
        return true;
      }
    }
    return false;
  }

  private boolean isContinueOrBackOnInvasive(Observation obs) {
    String value = ObservationUtils.getValueAsString(obs);
    return ("Continue".equals(value) || "Patient back on Invasive".equals(value) ||
            "Trial in progress".equals(value));
  }

  private boolean isDiscontinueOrPatientTakenOff(Observation obs) {
    String value = ObservationUtils.getValueAsString(obs);
    return ("Discontinue".equals(value) || "Patient taken off".equals(value) ||
            "Trial terminated".equals(value));
  }

  private boolean isYes(Observation obs) {
    String value = ObservationUtils.getValueAsString(obs);
    return ("Yes".equals(value));
  }
}
