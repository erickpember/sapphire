// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates flag codes.
 */
public enum FlagCodeEnum implements SystemDefinedCode<String> {
  UNKNOWN("UNKNOWN"),
  ADVANCE_DIRECTIVE("AD"),
  PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT("POLST"),
  PATIENT_CARE_CONFERENCE_NOTE("PATIENT_CARE_CONFERENCE_NOTE");

  private static final String SYSTEM = "http://datafascia.com/code/Flag";

  private final String code;

  private static final CodeToEnumMapper<String, FlagCodeEnum> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(FlagCodeEnum.class);

  FlagCodeEnum(String code) {
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
  public static Optional<FlagCodeEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
