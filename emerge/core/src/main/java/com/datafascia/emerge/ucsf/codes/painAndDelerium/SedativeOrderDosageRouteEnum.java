// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf.codes.painAndDelerium;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates the dosage routes in sedative orders in the pain and delirium group for Emerge.
 */
public enum SedativeOrderDosageRouteEnum implements Code<String> {
  INTERMITTENT_IV("Intermittent IV"),
  INTERMITTENT_ENTERAL("Intermittent Enteral"),
  CONTINUOUS_INFUSION_IV("Continuous Infusion IV");

  private final String code;

  private static final CodeToEnumMapper<String, SedativeOrderDosageRouteEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(SedativeOrderDosageRouteEnum.class);

  SedativeOrderDosageRouteEnum(String code) {
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
  public static Optional<SedativeOrderDosageRouteEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
