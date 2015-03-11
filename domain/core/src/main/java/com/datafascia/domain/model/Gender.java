// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates administrative genders. The gender codes are from
 * http://www.hl7.org/implement/standards/fhir/valueset-administrative-gender.html
 */
public enum Gender implements Code<String> {
  UNKNOWN("UNK"),
  FEMALE("F"),
  MALE("M"),
  UNDIFFERENTIATED("UN");

  private String code;

  private static final CodeToEnumMapper<String, Gender> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(Gender.class);

  private Gender(String code) {
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
  public static Optional<Gender> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
