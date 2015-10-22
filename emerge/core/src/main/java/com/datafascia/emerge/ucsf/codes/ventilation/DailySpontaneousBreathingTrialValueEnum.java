// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes.ventilation;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates the value field for the daily spontaneous breathing trial for Emerge.
 */
public enum DailySpontaneousBreathingTrialValueEnum implements Code<String> {
  GIVEN("Given"),
  NOT_GIVEN("Not Given"),
  CONTRAINDICATED("Contraindicated");

  private final String code;

  private static final CodeToEnumMapper<String, DailySpontaneousBreathingTrialValueEnum>
      CODE_TO_ENUM_MAPPER = new CodeToEnumMapper<>(DailySpontaneousBreathingTrialValueEnum.class);

  DailySpontaneousBreathingTrialValueEnum(String code) {
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
  public static Optional<DailySpontaneousBreathingTrialValueEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
