// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates types of Observation codes.
 */
public enum ObservationCodeEnum implements SystemDefinedCode<String> {
  ETT_INVASIVE_VENT_STATUS("304890042"),
  TRACH_INVASIVE_VENT_STATUS("304890045"),
  INTUBATION("304890038"),
  EXTUBATION("304890039"),
  ETT_INVASIVE_VENT_INITIATION("304890040"),
  ETT_ONGOING_INVASIVE_VENT("304890041"),
  TRACH_INVASIVE_VENT_INITIATION("304890043"),
  TRACH_ONGOING_INVASIVE_VENT("304890044"),
  VENT_MODE("304890046"),
  BREATH_TYPE("304890047"),
  NON_INVASIVE_DEVICE_MODE("3045000561"),
  INR("INR"),
  PLT("PLT"),
  PTT("PTT");

  private static final String SYSTEM = "http://datafascia.com/code/MedicationOrder";

  private final String code;

  private static final CodeToEnumMapper<String, ObservationCodeEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(ObservationCodeEnum.class);

  ObservationCodeEnum(String code) {
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
  public static Optional<ObservationCodeEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
