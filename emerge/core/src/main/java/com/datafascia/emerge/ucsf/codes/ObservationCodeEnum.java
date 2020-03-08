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
 * Enumerates types of Observation codes.
 */
public enum ObservationCodeEnum implements SystemDefinedCode<String> {
  ADMISSION_HEIGHT("HT"),
  CLINICAL_HEIGHT("304894102"),
  ADMISSION_WEIGHT("WT"),
  CLINICAL_WEIGHT("304894103"),
  DOSING_WEIGHT("304894104"),

  MOBILITY_SCORE("30489003"),

  INTUBATION("304890038"),
  EXTUBATION("304890039"),
  ETT_INVASIVE_VENT_STATUS("304890042"),
  ETT_INVASIVE_VENT_INITIATION("304890040"),
  ETT_ONGOING_INVASIVE_VENT("304890041"),
  TRACH_INVASIVE_VENT_STATUS("304890045"),
  TRACH_INVASIVE_VENT_INITIATION("304890043"),
  TRACH_ONGOING_INVASIVE_VENT("304890044"),
  VENT_MODE("304890046"),
  BREATH_TYPE("304890047"),
  TIDAL_VOLUME("304890051"),
  NON_INVASIVE_DEVICE_MODE("3045000561"),
  INLINE_PLACEMENT("304890061"),
  AIRWAY_DEVICE("3045000461"),
  AIRWAY_DEVICE_CODE("304894155"),
  LINE_REMOVAL_DATE("304890084"),
  LINE_REMOVAL_TIME("304890085"),

  SPONTANEOUS_BREATHING_TRIAL("304890064"),
  SPONTANEOUS_BREATHING_TRIAL_CONTRAINDICATED("304890063"),
  PRESSURE_SUPPORT("304203920"),
  FIO2("304890066"),
  PEEP("304890067"),
  TRAIN_OF_FOUR("304500964"),
  HEAD_OF_BED("304890036"),
  SUBGLOTTIC_SUCTION("304890059"),
  ORAL_CARE("304890060"),
  COOLING_PAD_STATUS("3045000709"),
  COOLING_PAD_PATIENT_TEMPERATURE("3045000458"),
  COOLING_PAD_WATER_TEMPERATURE("304894100"),
  TEMPERATURE("3045000001"),

  INR("INR"),
  PLT("PLT"),
  PTT("PTT"),

  BENZODIAZEPINE_AVOIDANCE("3045000404"),
  CPOT("304890016"),
  RASS("304890022"),
  ACTUAL_RASS("3045000021"),
  CAM_ICU("304890023"),
  NUMERICAL_PAIN_01("304890008"),
  NUMERICAL_PAIN_02("304890009"),
  NUMERICAL_PAIN_03("304890010"),
  NUMERICAL_PAIN_04("304890011"),
  VERBAL_PAIN_01("304890012"),
  VERBAL_PAIN_02("304890013"),
  VERBAL_PAIN_03("304890014"),
  VERBAL_PAIN_04("304890015"),
  PAIN_GOAL_01("304894105"),
  PAIN_GOAL_02("304890004"),
  PAIN_GOAL_03("304890005"),
  PAIN_GOAL_04("304890006"),
  SEDATION_WAKE_UP("304890033"),

  NEEDS_ASSESSMENT("304890072"),

  MECHANICAL_PPX_DEVICES("304890073"),
  MECHANICAL_PPX_INTERVENTIONS("304890074"),

  END_OF_OBSERVATION_ENUM("99999999");

  private static final String SYSTEM = "http://datafascia.com/code/Observation";

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
  public static Optional<ObservationCodeEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
