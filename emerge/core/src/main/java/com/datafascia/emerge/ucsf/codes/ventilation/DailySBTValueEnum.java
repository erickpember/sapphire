// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes.ventilation;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates the value field for the daily spontaneous breathing trial for Emerge.
 */
public enum DailySBTValueEnum implements SystemDefinedCode<String> {
  GIVEN("Given"),
  NOT_GIVEN("Not Given"),
  CONTRAINDICATED("Contraindicated");

  private static final String SYSTEM
      = "http://datafascia.com/code/DailySpontaneousBreathingTrialValue";

  private final String code;

  private static final CodeToEnumMapper<String, DailySBTValueEnum>
      CODE_TO_ENUM_MAPPER = new CodeToEnumMapper<>(DailySBTValueEnum.class);

  DailySBTValueEnum(String code) {
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
  public static Optional<DailySBTValueEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}