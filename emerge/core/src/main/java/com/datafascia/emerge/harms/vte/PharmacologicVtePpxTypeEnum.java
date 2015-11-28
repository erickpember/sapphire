// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vte;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.emerge.ucsf.codes.MedsSetEnum;
import java.util.Optional;

/**
 * Enumeration of pharmacologic VTE prophylaxis types.
 */
public enum PharmacologicVtePpxTypeEnum implements Code<String> {
  INTERMITTENT_ENOXAPARIN(MedsSetEnum.INTERMITTENT_ENOXAPARIN.getCode()),
  INTERMITTENT_HEPARIN_SC(MedsSetEnum.INTERMITTENT_HEPARIN_SC.getCode());

  private final String code;

  private static final CodeToEnumMapper<String, PharmacologicVtePpxTypeEnum> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(PharmacologicVtePpxTypeEnum.class);

  PharmacologicVtePpxTypeEnum(String code) {
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
  public static Optional<PharmacologicVtePpxTypeEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
