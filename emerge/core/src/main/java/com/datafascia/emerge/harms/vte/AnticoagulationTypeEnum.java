// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import java.util.Optional;

/**
 * Enumeration of anticoagulation types.
 */
public enum AnticoagulationTypeEnum implements Code<String> {
  CONTINUOUS_INFUSION_HEPARIN_IV(MedsSetEnum.CONTINUOUS_INFUSION_HEPARIN_IV.getCode()),
  CONTINUOUS_INFUSION_ARGATROBAN_IV(MedsSetEnum.CONTINUOUS_INFUSION_ARGATROBAN_IV.getCode()),
  CONTINUOUS_INFUSION_BIVALIRUDIAN_IV(MedsSetEnum.CONTINUOUS_INFUSION_BIVALIRUDIAN_IV.getCode()),
  INTERMITTENT_ENOXAPARIN(MedsSetEnum.INTERMITTENT_ENOXAPARIN.getCode()),
  INTERMITTENT_DABIGATRAN_ENTERAL(MedsSetEnum.INTERMITTENT_DABIGATRAN_ENTERAL.getCode()),
  INTERMITTENT_APIXABAN_ENTERAL(MedsSetEnum.INTERMITTENT_APIXABAN_ENTERAL.getCode()),
  INTERMITTENT_RIVAROXABAN_ENTERAL(MedsSetEnum.INTERMITTENT_RIVAROXABAN_ENTERAL.getCode()),
  INTERMITTENT_EDOXABAN_ENTERAL(MedsSetEnum.INTERMITTENT_EDOXABAN_ENTERAL.getCode()),
  INTERMITTENT_FONDAPARINUX_SC(MedsSetEnum.INTERMITTENT_FONDAPARINUX_SC.getCode()),
  INTERMITTENT_WARFARIN_ENTERAL(MedsSetEnum.INTERMITTENT_WARFARIN_ENTERAL.getCode());

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
