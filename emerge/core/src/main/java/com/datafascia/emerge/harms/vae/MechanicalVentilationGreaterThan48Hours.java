// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.google.common.collect.ImmutableSet;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;

/**
 * Mechanical ventilation greater than 48 hours implementation
 */
public class MechanicalVentilationGreaterThan48Hours {
  private static final Set<String> INTERMITTENT_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode(),
      ObservationCodeEnum.ETT_ONGOING_INVASIVE_VENT.getCode(),
      ObservationCodeEnum.TRACH_INVASIVE_VENT_INITIATION.getCode(),
      ObservationCodeEnum.TRACH_ONGOING_INVASIVE_VENT.getCode());

  private static final Set<String> VENT_STATUS_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(),
      ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode());

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Ventilated ventilatedImpl;

  /**
   * Checks if there is mechanical ventilation greater than 48 hours.
   *
   * @param encounter
   *     encounter to check
   * @return true if there is an observation in this encounter that meets the conditions
   */
  public boolean isMechanicalVentilationGreaterThan48Hours(Encounter encounter) {
    Observations observations =
        apiClient.getObservationClient().list(encounter.getId().getIdPart());

    Instant now = Instant.now(clock);

    if (!ventilatedImpl.isVentilated(encounter)) {
      return false;
    }

    return isMechanicalVentilationGreaterThan48Hours(observations, now);
  }

  /**
   * Checks if there is mechanical ventilation greater than 48 hours.
   * Encapsulates non-API-dependent logic.
   * Does not include test for ventilated.
   *
   * @param observations
   *     All observations for this encounter.
   * @param now
   *     The current time.
   * @return true if there is an observation in this encounter that meets the conditions
   */
  public boolean isMechanicalVentilationGreaterThan48Hours(Observations observations, Instant now) {
    Instant fortyEightHoursAgo = now.minus(48, ChronoUnit.HOURS);
    Instant seventyTwoHoursAgo = now.minus(72, ChronoUnit.HOURS);

    List<Observation> newEttInvasiveAndTrachInvasiveVentStatuses = observations.list(
        VENT_STATUS_OBSERVATION_CODES, fortyEightHoursAgo, null);

    boolean thereAreNoDiscontinueETTObservations = true;
    for (Observation newEttOrTrachStatus : newEttInvasiveAndTrachInvasiveVentStatuses) {
      if (ObservationUtils.getValueAsString(newEttOrTrachStatus).equals("Patient taken off")
          || ObservationUtils.getValueAsString(newEttOrTrachStatus).equals("Discontinue")) {
        thereAreNoDiscontinueETTObservations = false;
      }
    }

    List<Observation> intermittentVentilationTypes = observations.list(
        INTERMITTENT_OBSERVATION_CODES, seventyTwoHoursAgo, fortyEightHoursAgo);
    for (Observation anyIntermittentVentilationType : intermittentVentilationTypes) {
      if (thereAreNoDiscontinueETTObservations &&
          ObservationUtils.getValueAsString(anyIntermittentVentilationType).equals("Yes")) {
        // Yes: Intermittent
        return true;
      }
    }

    List<Observation> oldEttInvasiveAndTrachInvasiveVentStatuses = observations.list(
        VENT_STATUS_OBSERVATION_CODES, seventyTwoHoursAgo, fortyEightHoursAgo);
    for (Observation ettOrTrachStatus : oldEttInvasiveAndTrachInvasiveVentStatuses) {
      if (thereAreNoDiscontinueETTObservations &&
          (ObservationUtils.getValueAsString(ettOrTrachStatus).equals("Patient back on Invasive")
          || ObservationUtils.getValueAsString(ettOrTrachStatus).equals("Continue"))) {
        // Yes: Intermittent
        return true;
      }
    }

    return false;
  }
}
