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

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates the contraindicated reason field for the daily spontaneous breathing trial for Emerge.
 */
public enum DailySpontaneousBreathingTrialContraindicatedEnum implements Code<String> {
  FIO2_OVER_50("FiO2 > 50%"),
  PEEP_OVER_8("PEEP > 8"),
  RECEIVING_NMBA("Receiving NMBA"),
  LIMITED_RESPIRATORY_EFFORT("Limited Respiratory Effort"),
  CLINICALLY_UNSTABLE("Clinically Unstable"),
  OTHER("Other");

  private final String code;

  private static final CodeToEnumMapper<String, DailySpontaneousBreathingTrialContraindicatedEnum>
      CODE_TO_ENUM_MAPPER =
          new CodeToEnumMapper<>(DailySpontaneousBreathingTrialContraindicatedEnum.class);

  DailySpontaneousBreathingTrialContraindicatedEnum(String code) {
    this.code = code;
  }

  @Override
  public String getCode() {
    return code;
  }

  /**
   * Converts code to enum constant.
   *
   * @param code
   *     input code
   * @return optional enum constant, empty if code is unknown
   */
  public static Optional<DailySpontaneousBreathingTrialContraindicatedEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
