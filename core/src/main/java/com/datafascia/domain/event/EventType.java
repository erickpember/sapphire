// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.event;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Event type
 */
public enum EventType implements Code<String> {
  UNKNOWN("UNKNOWN"),
  PATIENT_ADMIT("PATIENT_ADMIT"),
  PATIENT_DISCHARGE("PATIENT_DISCHARGE");

  private String code;

  private static final CodeToEnumMapper<String, EventType> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(EventType.class);

  private EventType(String code) {
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
  public static Optional<EventType> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
