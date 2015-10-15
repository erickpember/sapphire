// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import java.time.Clock;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/**
 * Determines if sequential compression devices are in use.
 */
public class SCDsInUse {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  private Date getEndOfLastShift() {
    ZonedDateTime now = ZonedDateTime.now(clock);
    int nowHour = now.getHour();
    int lastShiftEndHour = (nowHour > 7 && nowHour < 19) ? 7 : 19;
    ZonedDateTime lastShiftEnd = ZonedDateTime.of(
        now.getYear(),
        now.getMonthValue(),
        now.getDayOfMonth(),
        lastShiftEndHour,
        0,
        0,
        0,
        clock.getZone());
    return Date.from(lastShiftEnd.toInstant());
  }

  /**
   * Determines if sequential compression devices are in use.
   *
   * @param encounterId
   *     encounter to check.
   * @return true if SCDs are in use
   */
  public boolean isSCDsInUse(String encounterId) {
    Date endOfLastShift = getEndOfLastShift();

    List<Observation> mechanicalProphylaxisDevicesFull = apiClient.getObservationClient()
        .searchObservation(encounterId, "304890073", null);

    List<Observation> mechanicalProphylaxisInterventionsFull = apiClient.getObservationClient()
        .searchObservation(encounterId, "304890074", null);

    // Get observations since the end of the last nursing shift.
    List<Observation> mechanicalProphylaxisDevices = new ArrayList<>();
    for (Observation observation : mechanicalProphylaxisDevicesFull) {
      Date effective = ObservationUtils.getEffectiveDate(observation);
      if (effective.after(endOfLastShift)) {
        mechanicalProphylaxisDevices.add(observation);
      }
    }

    List<Observation> mechanicalProphylaxisInterventions = new ArrayList<>();
    for (Observation observation : mechanicalProphylaxisInterventionsFull) {
      Date effective = ObservationUtils.getEffectiveDate(observation);
      if (effective.after(endOfLastShift)) {
        mechanicalProphylaxisInterventions.add(observation);
      }
    }

    // Get freshest iterations of the observations.
    Observation freshestMechanicalProphylaxisDevice = ObservationUtils
        .findFreshestObservation(mechanicalProphylaxisDevices);
    String freshestMechanicalProphylaxisDeviceValue =
        ObservationUtils.getValueAsString(freshestMechanicalProphylaxisDevice);

    Observation freshestMechanicalProphylaxisIntervention = ObservationUtils
        .findFreshestObservation(mechanicalProphylaxisInterventions);
    String freshestMechanicalProphylaxisInterventionValue =
        ObservationUtils.getValueAsString(freshestMechanicalProphylaxisIntervention);

    return "Sequential compression device(s)".equals(freshestMechanicalProphylaxisDeviceValue)
        && ("On left lower extremity".equals(freshestMechanicalProphylaxisInterventionValue)
         || "On right lower extremity".equals(freshestMechanicalProphylaxisInterventionValue));
  }
}
