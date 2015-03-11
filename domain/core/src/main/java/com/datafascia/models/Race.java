// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates human races. The race codes, except for UNKNOWN, are from
 * http://www.hl7.org/implement/standards/fhir/v3/Race/
 */
public enum Race implements Code<String> {
  UNKNOWN("UNKNOWN"),
  AMERICAN_INDIAN("1002-5"),
  ASIAN("2028-9"),
  BLACK("2054-5"),
  OTHER("2131-1"),
  PACIFIC_ISLANDER("2076-8"),
  WHITE("2106-3");

  private String code;

  private static final CodeToEnumMapper<String, Race> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(Race.class);

  private Race(String code) {
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
  public static Optional<Race> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
