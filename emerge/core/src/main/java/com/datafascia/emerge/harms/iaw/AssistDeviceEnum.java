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
package com.datafascia.emerge.harms.iaw;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates possible values for "assist device" in the IAW mobility score.
 */
public enum AssistDeviceEnum implements SystemDefinedCode<String> {

  NOT_DOCUMENTED("Not Documented"),
  CEILING_LIFT("Ceiling Lift"),
  VERTICAL_DEPENDENT_LIFT("Vertical dependent lift"),
  NEURO_CHAIR("Neuro Chair"),
  LATERAL_TRANSFER_DEVICE("Lateral Transfer Device"),
  WHEELCHAIR("Wheelchair"),
  SIT_TO_STAND_DEVICE_POWERED("Sit-to-stand Device (Powered)"),
  SIT_TO_STAND_DEVICE_NON_POWERED("Sit-to-stand Device (Non-powered)"),
  TOILET_RISER("Toilet Riser"),
  SHOWER_CHAIR("Shower chair"),
  GAIT_BELT("Gait Belt"),
  WALKER("Walker"),
  CRUTCHES("Crutches"),
  CANE("Cane"),
  OTHER("Other"),
  NONE("None");

  private static final String SYSTEM = "http://datafascia.com/code/AssistDevice";

  private final String code;

  private static final CodeToEnumMapper<String, AssistDeviceEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(AssistDeviceEnum.class);

  AssistDeviceEnum(String code) {
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
  public static Optional<AssistDeviceEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
