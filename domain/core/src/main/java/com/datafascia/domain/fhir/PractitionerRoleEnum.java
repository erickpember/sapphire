// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates practitioner codes.
 */
public enum PractitionerRoleEnum implements SystemDefinedCode<String> {
  ROLE_ICU_ATTENDING("ICU Attending"),
  ROLE_PRIMARY_ATTENDING("Primary Attending"),
  ROLE_ATTENDING_PROVIDER("Attending Provider"),
  ROLE_CLINICAL_NURSE("Clinical Nurse");

  private static final String SYSTEM = "http://datafascia.com/code/PractitionerRole";

  private final String code;

  private static final CodeToEnumMapper<String, PractitionerRoleEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(PractitionerRoleEnum.class);

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
