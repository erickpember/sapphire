// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes.ventilation;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates types of Ventilation modes as found in Observation values
 */
public enum VentModeObservationEnum implements SystemDefinedCode<String> {
  CPAP("CPAP"),
  SIMV("SIMV"),
  OTHER_SEE_COMMENT("Other (see comment)"),
  AC("AC"),
  PS("PS"),
  TC_SUPPORT("TC Support"),
  VOLUME_SUPPORT("Volume Support"),
  PA_SUPPORT("PA Support");

  private static final String SYSTEM = "http://datafascia.com/code/VentModeObservation";

  private final String code;

  private static final CodeToEnumMapper<String, VentModeObservationEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(VentModeObservationEnum.class);

  VentModeObservationEnum(String code) {
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
  public static Optional<VentModeObservationEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
