// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes.ventilation;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates the contraindicated field for the daily spontaneous breathing trial for Emerge.
 */
public enum DailySBTContraindicatedEnum implements SystemDefinedCode<String> {
  FIO2_OVER_50("FiO2 > 50%"),
  PEEP_OVER_8("PEEP > 8"),
  RECEIVING_NMBA("Receiving NMBA"),
  LIMITED_RESPIRATORY_EFFORT("Limited Respiratory Effort"),
  CLINICALLY_UNSTABLE("Clinically Unstable"),
  OTHER("Other");

  private static final String SYSTEM
      = "http://datafascia.com/code/DailySpontaneousBreathingTrialContraindicated";

  private final String code;

  private static final CodeToEnumMapper<String, DailySBTContraindicatedEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(DailySBTContraindicatedEnum.class);

  DailySBTContraindicatedEnum(String code) {
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
  public static Optional<DailySBTContraindicatedEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
