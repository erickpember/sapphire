// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.emerge.ucsf.codes.ventilation;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates types of Ventilation modes as reported to Emerge
 */
public enum VentModeEmergeEnum implements SystemDefinedCode<String> {
  ASSIST_CONTROL_VOLUME_CONTROL_ACVC("Assist Control Volume Control (ACVC)"),
  SYNCHRONOUS_INTERMITTENT_MANDATORY_VENTILATION_SIMV(
      "Synchronous Intermittent Mandatory Ventilation (SIMV)"),
  PRESSURE_SUPPORT_PS("Pressure Support (PS)"),
  VOLUME_SUPPORT_VS("Volume Support (VS)"),
  ASSIST_CONTROL_PRESSURE_CONTROL_ACPC("Assist Control Pressure Control (ACPC)"),
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
