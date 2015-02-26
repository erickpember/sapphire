// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates marital statuses. The marital status codes, except for UNKNOWN, are from
 * http://hl7.org/implement/standards/fhir/v3/MaritalStatus/
 */
public enum MaritalStatus implements Code<String> {
  UNKNOWN("UNKNOWN"),
  ANNULLED("A"),
  DIVORCED("D"),
  INTERLOCUTORY("I"),
  LEGALLY_SEPARATED("L"),
  MARRIED("M"),
  POLYGAMOUS("P"),
  NEVER_MARRIED("S"),
  DOMESTIC_PARTNER("T"),
  UNMARRIED("U"),
  WIDOWED("W");

  private String code;

  private static final CodeToEnumMapper<String, MaritalStatus> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(MaritalStatus.class);

  private MaritalStatus(String code) {
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
  public static Optional<MaritalStatus> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
