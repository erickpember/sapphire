// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

import ca.uhn.fhir.model.api.IValueSetEnumBinder;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates human races. The race codes, except for UNKNOWN, are from
 * http://hl7.org/fhir/v3/Race/index.html
 */
public enum RaceEnum implements SystemDefinedCode<String> {
  UNKNOWN("UNKNOWN"),
  AMERICAN_INDIAN("1002-5"),
  ASIAN("2028-9"),
  BLACK("2054-5"),
  OTHER("2131-1"),
  PACIFIC_ISLANDER("2076-8"),
  WHITE("2106-3");

  private static final String SYSTEM = "http://hl7.org/fhir/v3/Race";

  private final String code;

  private static final CodeToEnumMapper<String, RaceEnum> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(RaceEnum.class);

  RaceEnum(String code) {
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
  public static Optional<RaceEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }

  private static class RaceBinder implements IValueSetEnumBinder<RaceEnum> {
    @Override
    public String toCodeString(RaceEnum theEnum) {
      return theEnum.getCode();
    }

    @Override
    public String toSystemString(RaceEnum theEnum) {
      return theEnum.getSystem();
    }

    @Override
    public RaceEnum fromCodeString(String theCodeString) {
      return CODE_TO_ENUM_MAPPER.of(theCodeString).map(e -> e).orElse(null);
    }

    @Override
    public RaceEnum fromCodeString(String theCodeString, String theSystemString) {
      return fromCodeString(theCodeString);
    }
  }

  /**
   * Converts codes to their respective enumerated values
   */
  public static final IValueSetEnumBinder<RaceEnum> VALUESET_BINDER = new RaceBinder();
}
