// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.emerge.ucsf.codes;

import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates flag codes.
 */
public enum FlagCodeEnum implements SystemDefinedCode<String> {
  UNKNOWN("UNKNOWN"),
  ADVANCE_DIRECTIVE("AD"),
  PATIENT_CARE_CONFERENCE_NOTE("PATIENT_CARE_CONFERENCE_NOTE"),
  PHYSICIAN_ORDERS_FOR_LIFE_SUSTAINING_TREATMENT("POLST");

  private static final String SYSTEM = "http://datafascia.com/code/Flag";

  private final String code;

  private static final CodeToEnumMapper<String, FlagCodeEnum> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(FlagCodeEnum.class);

  FlagCodeEnum(String code) {
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
   * Converts this enum constant to codeable concept.
   *
   * @return codeable concept
   */
  public CodeableConceptDt toCodeableConcept() {
    return new CodeableConceptDt(getSystem(), getCode());
  }

  /**
   * Converts code to enum constant.
   *
   * @param code
   *     input code
   * @return optional enum constant, empty if code is unknown
   */
  public static Optional<FlagCodeEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
