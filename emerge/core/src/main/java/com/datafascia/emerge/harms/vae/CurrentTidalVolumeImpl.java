// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Computes VAE Harm Current Tidal Volume
 */
@Slf4j
public class CurrentTidalVolumeImpl {

  @Inject
  private ClientBuilder apiClient;

  private final BigDecimal ZERO = new BigDecimal("0");
  private final BigDecimal NEGATIVE_ONE = new BigDecimal("-1");

  /**
   * Computes VAE Harm Current Tidal Volume
   *
   * @param encounterId
   *     relevant encounter ID.
   * @return tidal volume specified encounter or 0 if not found
   */
  public BigDecimal getTidalVolume(String encounterId) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, -13);
    Date thirteenHoursAgo = cal.getTime();

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
            return NEGATIVE_ONE;
          } else {
            return ((QuantityDt) freshestVentSetTidalVolume.getValue()).getValue().abs();
          }
        case "Synchronous Intermittent Mandatory Ventilation (SIMV)":
          if (freshestBreathType != null && freshestBreathType.getValue().toString().equals(
              "Volume Control")) {
            if (freshestVentSetTidalVolume == null) {
              return NEGATIVE_ONE;
            } else {
              return ((QuantityDt) freshestVentSetTidalVolume.getValue()).getValue().abs();
            }
          }
        default:
          log.warn("Unknown vent mode value found: " + freshestVentMode.getValue().toString());
      }
    }
    return ZERO;
  }
}
