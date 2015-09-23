// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates procedure request codes.
 */
public enum ProcedureRequestCodeEnum implements SystemDefinedCode<String> {
  ATTENDING_PARTIAL("Attending Partial Code"),
  ATTENDING_DNR_DNI("Attending DNR/DNI"),
  RESIDENT_DNR_DNI("Resident DNR/DNI"),
  RESIDENT_PARTIAL("Resident Partial Code"),
  FULL("Full Code");

  private static final String SYSTEM = "http://datafascia.com/code/ProcedureRequest";

  private final String code;

  private static final CodeToEnumMapper<String, ProcedureRequestCodeEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(ProcedureRequestCodeEnum.class);

  ProcedureRequestCodeEnum(String code) {
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
  public static Optional<ProcedureRequestCodeEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
