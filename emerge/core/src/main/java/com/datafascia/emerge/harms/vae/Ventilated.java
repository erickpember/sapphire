// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationEffectiveComparator;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.util.Arrays;

/**
 * Implements VAE Harm Ventilated
 */
public class Ventilated {
  // Private constructor disallows creating instances of this class.
  private Ventilated() {
  }

  /**
   * Implements VAE Harm Ventilated
   *
   * @param client
   *     API client.
   * @param encounterId
   *     Relevant encounter ID.
   * @return
   *     True if the conditions that indicate ventilation are met.
   */
  public static boolean ventilated(ClientBuilder client, String encounterId) {
    Observation freshestETTInvasiveVentStatus = ObservationUtils.findFreshestObservation(
        Arrays.asList(ObservationUtils.findFreshestObservationForCode(client, encounterId,
                ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode()),
            ObservationUtils.findFreshestObservationForCode(client, encounterId,
                ObservationCodeEnum.ETT_INVASIVE_VENT_STATUS.getCode())));

    Observation freshestTrachInvasiveVentStatus = ObservationUtils.findFreshestObservation(
        Arrays.asList(ObservationUtils.findFreshestObservationForCode(client, encounterId,
                ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode()),
            ObservationUtils.findFreshestObservationForCode(client, encounterId,
                ObservationCodeEnum.TRACH_INVASIVE_VENT_STATUS.getCode())));

    Observation freshestInvasiveVentStatus = ObservationUtils.findFreshestObservation(
        Arrays.asList(freshestETTInvasiveVentStatus, freshestTrachInvasiveVentStatus));

    Observation freshestIntubation = ObservationUtils.findFreshestObservationForCodeAndValue(
        client, encounterId, ObservationCodeEnum.INTUBATION.getCode(), "Yes");

    Observation freshestExtubation = ObservationUtils.findFreshestObservationForCodeAndValue(
        client, encounterId, ObservationCodeEnum.EXTUBATION.getCode(), "Yes");

    Observation freshestETTInvasiveVentInitiation = ObservationUtils.findFreshestObservationForCode(
        client, encounterId, ObservationCodeEnum.ETT_INVASIVE_VENT_INITIATION.getCode());

    Observation freshestETTOngoingInvasiveVent = ObservationUtils.findFreshestObservationForCode(
        client, encounterId, ObservationCodeEnum.ETT_ONGOING_INVASIVE_VENT.getCode());

    Observation freshestTrachInvasiveVentInitiation = ObservationUtils.
        findFreshestObservationForCode(
            client, encounterId, ObservationCodeEnum.TRACH_INVASIVE_VENT_INITIATION.getCode());

    Observation freshestTrachOngoingInvasiveVent = ObservationUtils.
        findFreshestObservationForCodeAndValue(client, encounterId,
            ObservationCodeEnum.TRACH_ONGOING_INVASIVE_VENT.getCode(), "Yes");

    ObservationEffectiveComparator comparator = new ObservationEffectiveComparator();

    if (freshestInvasiveVentStatus != null && ((freshestInvasiveVentStatus.getValue().toString()
        .equals("Continue")
        || freshestInvasiveVentStatus.getValue().toString().equals("Patient back on Invasive"))
        && comparator.compare(freshestExtubation, freshestInvasiveVentStatus) < 0)) {
      return true;
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
