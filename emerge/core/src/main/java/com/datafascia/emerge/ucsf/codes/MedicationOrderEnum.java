// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates types of medication orders.
 */
public enum MedicationOrderEnum implements SystemDefinedCode<String> {
  STRESS_ULCER_PROPHYLACTICS("Stress Ulcer Prophylactics");

  private static final String SYSTEM = "http://datafascia.com/code/MedicationOrder";

  private final String code;

  private static final CodeToEnumMapper<String, MedicationOrderEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(MedicationOrderEnum.class);

  MedicationOrderEnum(String code) {
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
  public static Optional<MedicationOrderEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}