// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Computes current tidal volume
 */
@Slf4j
public class CurrentTidalVolume {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  private static int getAbsValue(Observation observation) {
    return ((QuantityDt) observation.getValue()).getValue().abs().intValue();
  }

  /**
   * Computes current tidal volume.
   *
   * @param encounterId
   *     relevant encounter ID
   * @return current tidal volume, or 0 if not found
   */
  public int apply(String encounterId) {
    Instant now = Instant.now(clock);
    Date thirteenHoursAgo = Date.from(now.minus(13, ChronoUnit.HOURS));

    Observation freshestVentMode = ObservationUtils.findFreshestObservationForCode(
        apiClient, encounterId, ObservationCodeEnum.VENT_MODE.getCode());

    if (freshestVentMode != null) {
      Observation freshestVentSetTidalVolume = ObservationUtils
          .getFreshestByCodeAfterTime(apiClient, encounterId,
              ObservationCodeEnum.NON_INVASIVE_DEVICE_MODE.getCode(), thirteenHoursAgo);

      Observation freshestBreathType = ObservationUtils.findFreshestObservationForCode(
          apiClient, encounterId, ObservationCodeEnum.BREATH_TYPE.getCode());

      switch (freshestVentMode.getValue().toString()) {
        case "VolumeControl (AC)":
        case "VolumeSupport (VS)":
        case "Pressure Regulated Volume Control (PRVC)":
          if (freshestVentSetTidalVolume == null) {
            return -1;
          } else {
            return getAbsValue(freshestVentSetTidalVolume);
          }
        case "Synchronous Intermittent Mandatory Ventilation (SIMV)":
          if (freshestBreathType != null && freshestBreathType.getValue().toString().equals(
              "Volume Control")) {
            if (freshestVentSetTidalVolume == null) {
              return -1;
            } else {
              return getAbsValue(freshestVentSetTidalVolume);
            }
          }
          break;
        default:
          log.warn("Unknown vent mode value [{}]", freshestVentMode.getValue().toString());
      }
    }

    return 0;
  }
}