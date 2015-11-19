// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.ShiftUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import javax.inject.Inject;

/**
 * Determines if sequential compression devices are in use.
 */
public class SCDsInUse {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to SCDS In Use.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to SCDS In Use.
   */
  public static boolean isRelevant(Observation observation) {
    return ObservationCodeEnum.MECHANICAL_PPX_DEVICES.isCodeEquals(observation.getCode()) ||
           ObservationCodeEnum.MECHANICAL_PPX_INTERVENTIONS.isCodeEquals(observation.getCode());
  }

  /**
   * Determines if sequential compression devices are in use.
   *
   * @param encounterId
   *     encounter to check.
   * @return true if SCDs are in use
   */
  public boolean isSCDsInUse(String encounterId) {
    PeriodDt currentOrPriorShift = ShiftUtils.getCurrentOrPreviousShift(clock);

    Observation freshestDeviceFromShift = ObservationUtils
        .getFreshestByCodeInTimeFrame(apiClient, encounterId,
            ObservationCodeEnum.MECHANICAL_PPX_DEVICES.getCode(), currentOrPriorShift);

    String freshestDeviceValue =
        ObservationUtils.getValueAsString(freshestDeviceFromShift);

    Observation freshestInterventionFromShift = ObservationUtils
        .getFreshestByCodeInTimeFrame(apiClient, encounterId,
            ObservationCodeEnum.MECHANICAL_PPX_INTERVENTIONS.getCode(), currentOrPriorShift);

    String freshestInterventionValue =
        ObservationUtils.getValueAsString(freshestInterventionFromShift);

    return "Sequential compression device(s)".equals(freshestDeviceValue)
        && (freshestInterventionValue.contains("On left lower extremity") ||
            freshestInterventionValue.contains("On right lower extremity"));
  }
}
