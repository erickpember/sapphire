// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates the elements of TimeStampedMaybe as in the harm evidence schema.
 */
public enum MaybeEnum implements SystemDefinedCode<String> {
  YES("Yes"),
  NO("No"),
  CONTRAINDICATED("Contraindicated"),
  NOT_DOCUMENTED("Not Documented");

  private static final String SYSTEM = "http://datafascia.com/code/Maybe";

  private final String code;

  private static final CodeToEnumMapper<String, MaybeEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(MaybeEnum.class);

  MaybeEnum(String code) {
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
  public static Optional<MaybeEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
