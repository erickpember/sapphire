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
  BENZODIAZEPINE_AVOIDANCE("3045000404"),
  MOBILITY_SCORE("30489003"),
  INTUBATION("304890038"),
  EXTUBATION("304890039"),
  ETT_INVASIVE_VENT_INITIATION("304890040"),
  ETT_ONGOING_INVASIVE_VENT("304890041"),
  TRACH_INVASIVE_VENT_INITIATION("304890043"),
  TRACH_ONGOING_INVASIVE_VENT("304890044"),
  VENT_MODE("304890046"),
  BREATH_TYPE("304890047"),
  NON_INVASIVE_DEVICE_MODE("3045000561"),
  INLINE_PLACEMENT("304890061"),
  AIRWAY_DEVICE("3045000461"),
  INR("INR"),
  PLT("PLT"),
  PTT("PTT"),
  SPONTANEOUS_BREATHING_TRIAL("304890064"),
  SPONTANEOUS_BREATHING_TRIAL_CONTRAINDICATED("304890063"),
  PRESSURE_SUPPORT("304890067"),
  FIO2("304890068"),
  PEEP("304890066"),
  TRAIN_OF_FOUR("304500964"),
  HEAD_OF_BED("304890036"),
  SUBGLOTTIC_SUCTION("304890059"),
  ORAL_CARE("304890060"),
  CPOT("304890016"),
  RASS("304890022"),
  CAM_ICU("304890023"),
  NEEDS_ASSESSMENT("304890072"),
  NUMERICAL_PAIN_01("304890008"),
  NUMERICAL_PAIN_02("304890009"),
  NUMERICAL_PAIN_03("304890010"),
  NUMERICAL_PAIN_04("304890011"),
  VERBAL_PAIN_01("304890012"),
  VERBAL_PAIN_02("304890013"),
  VERBAL_PAIN_03("304890014"),
  VERBAL_PAIN_04("304890015"),
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
