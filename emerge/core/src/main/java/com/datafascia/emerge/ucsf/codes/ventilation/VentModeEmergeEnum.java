// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes.ventilation;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates types of Ventilation modes as reported to Emerge
 */
public enum VentModeEmergeEnum implements SystemDefinedCode<String> {
  VOLUME_CONTROL_AC("Volume Control (AC)"),
  SYNCHRONOUS_INTERMITTENT_MANDATORY_VENTILATION_SIMV(
      "Synchronous Intermittent Mandatory Ventilation (SIMV)"),
  PRESSURE_SUPPORT_PS("Pressure Support (PS)"),
  VOLUME_SUPPORT_VS("Volume Support (VS)"),
  PRESSURE_CONTROL_PC("Pressure Control (PC)"),
  PRESSURE_REGULATED_VOLUME_CONTROL_PRVC("Pressure Regulated Volume Control (PRVC)"),
  AIRWAY_PRESSURE_RELEASE_VENTILATION_APRV("Airway Pressure Release Ventilation (APRV)"),
  HIGH_FREQUENCY_OSCILLATION_HFO("High Frequency Oscillation (HFO)"),
  OTHER("Other");

  private static final String SYSTEM = "http://datafascia.com/code/VentModeEmerge";

  private final String code;

  private static final CodeToEnumMapper<String, VentModeEmergeEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(VentModeEmergeEnum.class);

  VentModeEmergeEnum(String code) {
    this.code = code;
  }

  @Override
  public String getCode() {
    return code;
  }

  @Override
  public String getSystem() {
    return SYSTEM;
  }

  /**
   * Converts code to enum constant.
   *
   * @param code
   *     input code
   * @return optional enum constant, empty if code is unknown
   */
  public static Optional<VentModeEmergeEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
