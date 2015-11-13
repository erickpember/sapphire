// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.pain;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.ShiftUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import javax.inject.Inject;
import joptsimple.internal.Strings;
import lombok.extern.slf4j.Slf4j;

/**
 * Implements the Pain and Delirium Harms CAM-ICU UTA Reason
 */
@Slf4j
public class CamUtaReason {

  @Inject
  private ClientBuilder apiClient;

  @Inject
  private Clock clock;

  /**
   * Implements the Pain and Delirium Harms CAM-ICU UTA Reason
   *
   * @param encounterId
   *     encounter to check.
   * @return Cam UTA Reason, or {@code null} if not found.
   */
  public String getCamUtaReason(String encounterId) {
    PeriodDt currentOrPriorShift = ShiftUtils.getCurrentOrPreviousShift(clock);

    Observation freshestFromShift = ObservationUtils.getFreshestByCodeInTimeFrame(apiClient,
        encounterId, ObservationCodeEnum.CAM_ICU.getCode(), currentOrPriorShift);

    if (freshestFromShift == null || Strings.isNullOrEmpty(ObservationUtils.getValueAsString(
        freshestFromShift))) {
      return null;
    }

    switch (ObservationUtils.getValueAsString(freshestFromShift)) {
      case "UTA (RASS -4 or -5)":
        return "RASS Score -4 or -5";
      case "UTA (Language barrier)":
        return "Language Barrier";
      case "UTA (Developmental delay)":
        return "Developmental Delay";
      case "+":
      case "-":
        // Expected values for Cam Result, don't need to log a warning about these.
        return null;
      default:
        log.warn("Unexpected cam UTA value:" + ObservationUtils.getValueAsString(
            freshestFromShift) + " found in observation " + freshestFromShift.getId().getValue());
        return null;
    }
  }
}
