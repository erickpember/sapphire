// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
