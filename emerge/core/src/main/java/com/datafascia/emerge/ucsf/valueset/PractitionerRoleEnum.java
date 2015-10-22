// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.valueset;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates practitioner codes.
 */
public enum PractitionerRoleEnum implements SystemDefinedCode<String> {
  ICU_ATTENDING("ICU Attending"),
  PRIMARY_ATTENDING("Primary Attending"),
  ATTENDING_PROVIDER("Attending Provider"),
  CLINICAL_NURSE("Clinical Nurse");

  private final String code;

  private static final String SYSTEM = "http://datafascia.com/coding/PractitionerRole";

  private static final CodeToEnumMapper<String, PractitionerRoleEnum> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(PractitionerRoleEnum.class);

  PractitionerRoleEnum(String code) {
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
   * @param code input code
   * @return optional enum constant, empty if code is unknown
   */
  public static Optional<PractitionerRoleEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
