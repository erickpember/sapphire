// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Class to determine if SCDs are in use.
 */
public class SCDsInUse {

  /**
   * Determines if SCDs are in use.
   *
   * @param client The client builder to use.
   * @param encounterId The encounter to check.
   * @return If SCDs are in use.
   */
  public static boolean SCDsInUse(ClientBuilder client, String encounterId) {
    Date endOfLastShift = getEndOfLastShift();

    List<Observation> mechanicalProphylaxisDevicesFull = client.getObservationClient()
        .searchObservation(encounterId, "304890073", null);

    List<Observation> mechanicalProphylaxisInterventionsFull = client.getObservationClient()
        .searchObservation(encounterId, "304890074", null);

    // Get observations since the end of the last nursing shift.
    List<Observation> mechanicalProphylaxisDevices = new ArrayList<>();
    for (Observation observation : mechanicalProphylaxisDevicesFull) {
      if (observation.getIssued().after(endOfLastShift)) {
        mechanicalProphylaxisDevices.add(observation);
      }
    }

    List<Observation> mechanicalProphylaxisInterventions = new ArrayList<>();
    for (Observation observation : mechanicalProphylaxisInterventionsFull) {
      if (observation.getIssued().after(endOfLastShift)) {
        mechanicalProphylaxisInterventions.add(observation);
      }
    }

    // Get freshest iterations of the observations.
    Observation freshestMechanicalProphylaxisDevice = ObservationUtils
        .findFreshestObservation(mechanicalProphylaxisDevices);

    Observation freshestMechanicalProphylaxisIntervention = ObservationUtils
        .findFreshestObservation(mechanicalProphylaxisInterventions);

    if (freshestMechanicalProphylaxisDevice.getValue().equals("Sequential compression device(s)")
        && (freshestMechanicalProphylaxisIntervention.getValue().equals("On right lower extremity")
        || freshestMechanicalProphylaxisIntervention.getValue().equals("On left lower extremity")))
    {
      return true;
    }

    return false;
  }

  /**
   * Returns the time the last shift ended.
   *
   * @return The time the last shift ended.
   */
  private static Date getEndOfLastShift() {
    Calendar calendar = Calendar.getInstance();

    calendar.set(Calendar.MINUTE, 0);

    int nowHour = calendar.get(Calendar.HOUR_OF_DAY);
    if (nowHour > 7 && nowHour < 19) {
      calendar.set(Calendar.HOUR, 7);
      return calendar.getTime();
    } else {
      calendar.set(Calendar.HOUR, 19);
      return calendar.getTime();
    }
  }
}
