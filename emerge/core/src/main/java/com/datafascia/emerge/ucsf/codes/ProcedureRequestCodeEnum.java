// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates procedure request codes.
 */
public enum ProcedureRequestCodeEnum implements SystemDefinedCode<String> {
  ATTENDING_PARTIAL("Attending Partial Code"),
  ATTENDING_DNR_DNI("Attending DNR/DNI"),
  RESIDENT_DNR_DNI("Resident DNR/DNI"),
  RESIDENT_PARTIAL("Resident Partial Code"),
  FULL("Full Code"),
  TARGET_RASS_0_ALERT_AND_CALM("Target RASS 0: Alert and Calm (acceptable range -1 to +1)"),
  TARGET_RASS_NEG1_DROWSY("Target RASS -1: Drowsy (acceptable range -2 to 0)"),
  TARGET_RASS_NEG2_LIGHT_SEDATION("Target RASS -2: Light Sedation (acceptable range -3 to -1)"),
  TARGET_RASS_NEG2_MODERATE_SEDATION(
      "Target RASS -3: Moderate Sedation (acceptable range -4 to -2)"),
  TARGET_RASS_NEG4_DEEP_SEDATION("Target RASS -4: Deep Sedation (acceptable range -5 to -3)"),
  TARGET_RASS_NEG5_UNAROUSABLE("Target RASS -5: Unarousable (acceptable range -5 to -4)"),
  TARGET_RASS_NA_NMBA("Target RASS N/A: Patient on NMBA"),
  TARGET_RASS_NA_SEIZURES_STATUS_EPILEPTICUS("Target RASS N/A: Seizures/Status Epilepticus");

  private static final String SYSTEM = "http://datafascia.com/code/ProcedureRequest";

  private final String code;

  private static final CodeToEnumMapper<String, ProcedureRequestCodeEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(ProcedureRequestCodeEnum.class);

  ProcedureRequestCodeEnum(String code) {
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
  public static Optional<ProcedureRequestCodeEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
