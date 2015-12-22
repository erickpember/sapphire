// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.EncounterUtils;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
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

  @Inject
  private VentilationModeImpl ventilationModeImpl;

  private static int getAbsValue(Observation observation) {
    return ((QuantityDt) observation.getValue()).getValue().abs().intValue();
  }

  /**
   * Computes current tidal volume.
   *
   * @param encounter
   *     relevant encounter
   * @return current tidal volume, or 0 if not found
   */
  public int apply(Encounter encounter) {
    Instant icuAdmitTime = EncounterUtils.getIcuPeriodStart(encounter);
    Instant now = Instant.now(clock);
    Instant thirteenHoursAgo = now.minus(13, ChronoUnit.HOURS);

    String ventilationMode = ventilationModeImpl.getVentilationMode(encounter);

    Observations observations = apiClient.getObservationClient()
        .list(encounter.getId().getIdPart());
    Optional<Observation> freshestVentSetTidalVolume = observations.findFreshest(
        ObservationCodeEnum.TIDAL_VOLUME.getCode(), thirteenHoursAgo, null);

    Optional<Observation> freshestBreathType = observations.findFreshest(
        ObservationCodeEnum.BREATH_TYPE.getCode(), icuAdmitTime, null);

    switch (ventilationMode) {
      case "Assist Control Volume Control (ACVC)":
      case "Volume Support (VS)":
      case "Pressure Regulated Volume Control (PRVC)":
        if (!freshestVentSetTidalVolume.isPresent()) {
          return -1;
        } else {
          return getAbsValue(freshestVentSetTidalVolume.get());
        }
      case "Synchronous Intermittent Mandatory Ventilation (SIMV)":
        if (freshestBreathType.isPresent() && "Volume Control".equals(ObservationUtils
            .getValueAsString(freshestBreathType.get()))) {
          if (!freshestVentSetTidalVolume.isPresent()) {
            return -1;
          } else {
            return getAbsValue(freshestVentSetTidalVolume.get());
          }
        }
        break;
      case "Pressure Support (PS)":
      case "Airway Pressure Release Ventilation (APRV)":
      case "Indeterminate":
      case "Assist Control Pressure Control (ACPC)":
        break;
      default:
        log.warn("Ventilation mode value [{}] does not set tidal volume", ventilationMode);
    }

    return 0;
  }
}
