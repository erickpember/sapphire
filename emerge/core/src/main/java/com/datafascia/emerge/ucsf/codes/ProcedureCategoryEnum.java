// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates procedure categories.
 */
public enum ProcedureCategoryEnum implements SystemDefinedCode<String> {
  CENTRAL_LINE("CENTRAL_LINE"),
  UNKNOWN("UNKNOWN");

  private static final String SYSTEM = "http://datafascia.com/coding/ProcedureCategory";

  private final String code;

  private static final CodeToEnumMapper<String, ProcedureCategoryEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(ProcedureCategoryEnum.class);

  ProcedureCategoryEnum(String code) {
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
   * Converts enum constant to codeable concept.
   *
   * @return codeable concept
   */
  public CodeableConceptDt toCodeableConcept() {
    return new CodeableConceptDt(getSystem(), getCode());
  }

  /**
   * Converts code to enum constant.
   *
   * @param code
   *     input code
   * @return optional enum constant, empty if code is unknown
   */
  public static Optional<ProcedureCategoryEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
