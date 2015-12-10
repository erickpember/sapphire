// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/**
 * Mechanical ventilation greater than 48 hours implementation
 */
public class MechanicalVentilationGreaterThan48Hours {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Ventilated ventilatedImpl;

  private static final boolean DEFAULT_RESULT = false;

  /**
   * Checks if there is mechanical ventilation greater than 48 hours.
   *
   * @param encounterId
   *     encounter to check
   * @return true if there is an observation in this encounter that meets the conditions
   */
  public boolean isMechanicalVentilationGreaterThan48Hours(String encounterId) {
    Instant now = Instant.now(clock);
    Date fortyEightHoursAgo = Date.from(now.minus(48, ChronoUnit.HOURS));

    // grouping of negative short-circuit logic
    if (!ventilatedImpl.isVentilated(encounterId)) {
      return false;
    }

    List<Observation> extubations = ObservationUtils.getByCodeAfterTime(apiClient,
        encounterId, ObservationCodeEnum.EXTUBATION.getCode(), fortyEightHoursAgo);
    for (Observation extubation : extubations) {
      if (extubation.getValue().toString().equals("Yes")) {
        return false;
      }
    }

    List<Observation> ettInvasiveVentStatuses = ObservationUtils.getByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode(),
        fortyEightHoursAgo);
    for (Observation ettInvasiveVentStatus : ettInvasiveVentStatuses) {
      if (ettInvasiveVentStatus.getValue().toString().equals("Patient taken off")
          || ettInvasiveVentStatus.getValue().toString().equals("Discontinue")) {
        return false;
      }
    }

    List<Observation> trachInvasiveVentStatuses = ObservationUtils.getByCodeAfterTime(
        apiClient, encounterId, ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode(),
        fortyEightHoursAgo);
    for (Observation trachInvasiveVentStatus : trachInvasiveVentStatuses) {
      if (trachInvasiveVentStatus.getValue().toString().equals("Patient taken off")
          || trachInvasiveVentStatus.getValue().toString().equals("Discontinue")) {
        return false;
      }
    }

    // grouping of positive short-circuit logic
    List<Observation> intubations = apiClient.getObservationClient().searchObservation(encounterId,
        ObservationCodeEnum.INTUBATION.getCode(), null);
    for (Observation intubation : intubations) {
      if (intubation.getValue().toString().equals("Yes")
          && !ObservationUtils.isAfter(intubation, fortyEightHoursAgo)) {
        return true;
      }
    }

    List<Observation> ettInvasiveVentInitiations = apiClient.getObservationClient()
        .searchObservation(encounterId, ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode(),
            null);
    for (Observation ettInvasiveVentInitiation : ettInvasiveVentInitiations) {
      if (ettInvasiveVentInitiation.getValue().toString().equals("Yes")) {
        return true;
      }
    }

    List<Observation> ettOngoingInvasiveVents = apiClient.getObservationClient().searchObservation(
        encounterId, ObservationCodeEnum.ETT_ONGOING_INVASIVE_VENT.getCode(), null);
    for (Observation ettOngoingInvasiveVent : ettOngoingInvasiveVents) {
      if (ettOngoingInvasiveVent.getValue().toString().equals("Yes")) {
        return true;
      }
    }

    List<Observation> trachInvasiveVentInitiations = apiClient.getObservationClient()
        .searchObservation(
            encounterId, ObservationCodeEnum.TRACH_INVASIVE_VENT_INITIATION.getCode(), null);
    for (Observation trachInvasiveVentInitiation : trachInvasiveVentInitiations) {
      if (trachInvasiveVentInitiation.getValue().toString().equals("Yes")) {
        return true;
      }
    }

    List<Observation> trachOngoingInvasiveVents = apiClient.getObservationClient()
        .searchObservation(encounterId, ObservationCodeEnum.TRACH_ONGOING_INVASIVE_VENT.getCode(),
            null);
    for (Observation trachOngoingInvasiveVent : trachOngoingInvasiveVents) {
      if (trachOngoingInvasiveVent.getValue().toString().equals("Yes")) {
        return true;
      }
    }

    return DEFAULT_RESULT;
  }
}
