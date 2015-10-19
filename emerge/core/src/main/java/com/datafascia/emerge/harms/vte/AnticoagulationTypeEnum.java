// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumeration of anticoagulation types.
 */
public enum AnticoagulationTypeEnum implements Code<String> {
  CONTINUOUS_INFUSION_HEPARIN_IV("Continuous Infusion Heparain IV"),
  CONTINUOUS_INFUSION_ARGATROBAN_IV("Continuous Infusion Argatroban IV"),
  CONTINUOUS_INFUSION_BIVALIRUDIAN_IV("Continuous Infusion Bivalirudian IV"),
  INTERMITTENT_ENOXAPARIN_SC("Intermittent Enoxaparin SC"),
  INTERMITTENT_DABIGATRAN_ENTERAL("Intermittent Dabigatran Enteral"),
  INTERMITTENT_APIXABAN_ENTERAL("Intermittent Apixaban Enteral"),
  INTERMITTENT_RIVAROXABAN_ENTERAL("Intermittent Rivaroxaban Enteral"),
  INTERMITTENT_EDOXABAN_ENTERAL("Intermittent Edoxaban Enteral"),
  INTERMITTENT_FONDAPARINUX_SC("Intermittent Fondaparinux SC"),
  INTERMITTENT_WARFARIN_ENTERAL("Intermittent Warfarin Enteral");

  private final String code;

  private static final CodeToEnumMapper<String, AnticoagulationTypeEnum> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(AnticoagulationTypeEnum.class);

  AnticoagulationTypeEnum(String code) {
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
  public static Optional<AnticoagulationTypeEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
