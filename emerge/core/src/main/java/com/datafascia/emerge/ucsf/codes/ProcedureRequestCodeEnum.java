// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates procedure request codes.
 */
public enum ProcedureRequestCodeEnum implements SystemDefinedCode<String> {
  TARGET_RASS("124838"),
  HYPOTHERMIA_BLANKET_ORDER_1("57714"),
  HYPOTHERMIA_BLANKET_ORDER_2("139056"),
  HOB_FLAT("40739"),
  PRONE("57652"),
  SUPINE("41043"),
  LIE_FLAT("409211"),
  BED_REST_HOB_LESS_THAN_31("143391"),
  BED_REST_HOB_FLAT("40921"),
  HOB_LESS_THAN_10("138995"),
  HOB_LESS_THAN_30("40733"),
  VTE_PPX_CONTRAINDICATIONS("87882"),
  NO_VTE_PREVENTION_INDICATED("87881"),
  PLACE_SCDS("7820"),
  MAINTAIN_SCDS("7936"),
  REMOVE_SCDS("40999"),

  ATTENDING_PARTIAL("521"),
  ATTENDING_DNR_DNI("517"),
  RESIDENT_DNR_DNI("82934"),
  RESIDENT_PARTIAL("82935"),
  FULL_CODE("519"),

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
   * Checks if this enum constant's code equals the desired code.
   *
   * @param desiredCode
   *     code to match
   * @return true if this enum constant's code equals the desired code
   */
  public boolean isCodeEquals(CodeableConceptDt desiredCode) {
    return code.equals(desiredCode.getCodingFirstRep().getCode());
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
