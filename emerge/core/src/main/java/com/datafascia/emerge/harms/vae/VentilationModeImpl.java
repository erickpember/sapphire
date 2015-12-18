// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.api.client.Observations;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.datafascia.emerge.ucsf.codes.ventilation.BreathTypeEnum;
import com.datafascia.emerge.ucsf.codes.ventilation.NonInvasiveDeviceModeEnum;
import com.datafascia.emerge.ucsf.codes.ventilation.VentModeEmergeEnum;
import com.datafascia.emerge.ucsf.codes.ventilation.VentModeObservationEnum;
import com.google.common.collect.ImmutableSet;
import java.util.Optional;
import java.util.Set;
import javax.inject.Inject;

import static com.datafascia.api.client.Observations.isEffectiveAfter;

/**
 * Computes VAE Ventilation Mode
 */
public class VentilationModeImpl {

  private static final Set<String> RELEVANT_OBSERVATION_CODES = ImmutableSet.of(
      ObservationCodeEnum.VENT_MODE.getCode(),
      ObservationCodeEnum.BREATH_TYPE.getCode(),
      ObservationCodeEnum.NON_INVASIVE_DEVICE_MODE.getCode());

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to ventilation mode.
   *
   * @param observation
   *     observation
   * @return true if observation is relevant to ventilation mode
   */
  public static boolean isRelevant(Observation observation) {
    return RELEVANT_OBSERVATION_CODES.contains(observation.getCode().getCodingFirstRep().getCode());
  }

  /**
   * Computes VAE ventilation mode
   *
   * @param encounterId
   *     encounter to search
   * @return ventilation mode
   */
  public String getVentilationMode(String encounterId) {
    Observations observations = apiClient.getObservationClient().list(encounterId);

    Optional<Observation> freshestVentMode = observations.findFreshest(
        ObservationCodeEnum.VENT_MODE.getCode());

    Optional<Observation> freshestBreathType = observations.findFreshest(
        ObservationCodeEnum.BREATH_TYPE.getCode());

    Optional<Observation> freshestNonInvasiveDeviceMode = observations.findFreshest(
        ObservationCodeEnum.NON_INVASIVE_DEVICE_MODE.getCode());

    // For when either breath type or vent mode are fresher than non-invasive device mode
    if (freshestBreathType.isPresent() &&
        freshestVentMode.isPresent() &&
        (isEffectiveAfter(freshestVentMode, freshestNonInvasiveDeviceMode) ||
         isEffectiveAfter(freshestBreathType, freshestNonInvasiveDeviceMode))) {
      Optional<String> result = eitherBreathOrVentIsFreshest(
          freshestBreathType.get(), freshestVentMode.get());
      if (result.isPresent()) {
        return result.get();
      }
    }

    // For when breath type is fresher than non-invasive device mode
    if (freshestBreathType.isPresent() &&
        isEffectiveAfter(freshestBreathType, freshestNonInvasiveDeviceMode)) {
      Optional<String> result = breathIsFresherThanNonInvasive(freshestBreathType.get());
      if (result.isPresent()) {
        return result.get();
      }
    }

    // For when non-invasive device mode is fresher than both the freshest vent mode and breath type
    if (freshestBreathType.isPresent() &&
        freshestVentMode.isPresent() &&
        freshestNonInvasiveDeviceMode.isPresent() &&
        (isEffectiveAfter(freshestVentMode, freshestNonInvasiveDeviceMode) &&
         isEffectiveAfter(freshestBreathType, freshestNonInvasiveDeviceMode))) {
      Optional<String> result = nonInvasiveIsFresherThanVentOrBreath(
          freshestNonInvasiveDeviceMode.get());
      if (result.isPresent()) {
        return result.get();
      }
    }

    return "Indeterminate";
  }

  /**
   * Handles ventilated mode logic for cases where
   * either the freshest breath type observation or the freshest vent mode observation is newer
   * than the freshest non-invasive device mode observation.
   *
   * @param breath
   *     Freshest breath type observation found.
   * @param vent
   *     Freshest vent mode observation found.
   * @return
   *     Vent mode string if a match is found, otherwise empty.
   */
  private Optional<String> eitherBreathOrVentIsFreshest(Observation breath, Observation vent) {
    if (breath.getValue().toString().equals(BreathTypeEnum.VOLUME_CONTROL.getCode())
        && vent.getValue().toString().equals(VentModeObservationEnum.AC.getCode())) {
      return Optional.of(VentModeEmergeEnum.ASSIST_CONTROL_VOLUME_CONTROL_ACVC.getCode());
    }

    if ((breath.getValue().toString().equals(BreathTypeEnum.VOLUME_CONTROL.getCode()) || breath
        .getValue().toString().equals(BreathTypeEnum.PRESSURE_CONTROL.getCode())) && vent.getValue()
        .toString().equals(VentModeObservationEnum.SIMV.getCode())) {
      return Optional.of(VentModeEmergeEnum.SYNCHRONOUS_INTERMITTENT_MANDATORY_VENTILATION_SIMV
          .getCode());
    }

    if (breath.getValue().toString().equals(BreathTypeEnum.SPONTANEOUS.getCode())
        && (vent.getValue().toString().equals(VentModeObservationEnum.PS.getCode())
        || vent.getValue().toString().equals(VentModeObservationEnum.CPAP.getCode())
        || vent.getValue().toString().equals(VentModeObservationEnum.TC_SUPPORT
            .getCode()))) {
      return Optional.of(VentModeEmergeEnum.PRESSURE_SUPPORT_PS.getCode());
    }

    if (breath.getValue().toString().equals(BreathTypeEnum.VG.getCode()) || (vent.getValue()
        .toString().equals(VentModeObservationEnum.VOLUME_SUPPORT.getCode()))) {
      return Optional.of(VentModeEmergeEnum.VOLUME_SUPPORT_VS.getCode());
    }

    if (breath.getValue().toString().equals(BreathTypeEnum.PRESSURE_CONTROL.getCode())
        && vent.getValue().toString().equals(VentModeObservationEnum.AC.getCode())) {
      return Optional.of(VentModeEmergeEnum.ASSIST_CONTROL_PRESSURE_CONTROL_ACPC.getCode());
    }

    if (breath.getValue().toString().equals(BreathTypeEnum.DUAL_MODE_PRVC_OR_VC.getCode()) && vent
        .getValue().toString().equals(VentModeObservationEnum.PA_SUPPORT.getCode())) {
      return Optional.of(VentModeEmergeEnum.PRESSURE_REGULATED_VOLUME_CONTROL_PRVC.getCode());
    }

    if (breath.getValue().toString().equals(BreathTypeEnum.OTHER_SEE_COMMENT.getCode()) || vent
        .getValue().toString().equals(VentModeObservationEnum.OTHER_SEE_COMMENT.getCode())) {
      return Optional.of(VentModeEmergeEnum.OTHER.getCode());
    }

    return Optional.empty();
  }

  /**
   * Handles ventilated mode logic for cases where
   * the freshest breath type observation is fresher than the freshest non-invasive device mode
   * observation.
   *
   * @param breath
   *     Freshest breath type observation found.
   * @return
   *     Vent mode string if a match is found, otherwise empty.
   */
  private Optional<String> breathIsFresherThanNonInvasive(Observation breath) {
    if (breath.getValue().toString()
        .replaceAll("\\s","").equals(BreathTypeEnum.APRV_BILEVEL.getCode())) {
      return Optional.of(VentModeEmergeEnum.AIRWAY_PRESSURE_RELEASE_VENTILATION_APRV.getCode());
    }

    if (breath.getValue().toString().equals(BreathTypeEnum.HFOV.getCode())) {
      return Optional.of(VentModeEmergeEnum.HIGH_FREQUENCY_OSCILLATION_HFO.getCode());
    }
    return Optional.empty();
  }

  /**
   * Handles ventilated mode logic for cases where
   * the freshest non-invasive device mode is fresher than both the freshest breath type and
   * the freshest vent mode.
   *
   * @param nonInvasive
   *     Freshest non-invasive device mode observation found.
   * @return
   *     Vent mode string if a match is found, otherwise empty.
   */
  private Optional<String> nonInvasiveIsFresherThanVentOrBreath(Observation nonInvasive) {
    if (nonInvasive == null || nonInvasive.getValue() == null) {
      return Optional.empty();
    }

    if (ObservationUtils.getValueAsString(nonInvasive)
        .equals(NonInvasiveDeviceModeEnum.NPPV.getCode())
        || ObservationUtils.getValueAsString(nonInvasive)
            .equals(NonInvasiveDeviceModeEnum.CPAP.getCode())) {
      return Optional.of(VentModeEmergeEnum.PRESSURE_SUPPORT_PS.getCode());
    }

    if (ObservationUtils.getValueAsString(nonInvasive)
        .equals(NonInvasiveDeviceModeEnum.PCV.getCode())) {
      return Optional.of(VentModeEmergeEnum.ASSIST_CONTROL_PRESSURE_CONTROL_ACPC.getCode());
    }

    if (ObservationUtils.getValueAsString(nonInvasive)
        .equals(NonInvasiveDeviceModeEnum.AVAPS.getCode())) {
      return Optional.of(
          VentModeEmergeEnum.PRESSURE_REGULATED_VOLUME_CONTROL_PRVC.getCode());
    }

    if (ObservationUtils.getValueAsString(nonInvasive)
        .equals(NonInvasiveDeviceModeEnum.S_T.getCode()) ||
        ObservationUtils.getValueAsString(nonInvasive)
        .equals(NonInvasiveDeviceModeEnum.SISAP_BIPHASIC.getCode()) ||
        nonInvasive.getValue().toString().equals(
            NonInvasiveDeviceModeEnum.OTHER_SEE_COMMENT.getCode())) {
      return Optional.of(VentModeEmergeEnum.OTHER.getCode());
    }
    return Optional.empty();
  }
}
