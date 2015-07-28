// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.event;

import com.datafascia.common.persist.Code;
import com.datafascia.common.persist.CodeToEnumMapper;
import java.util.Optional;

/**
 * Enumerates central line types
 */
public enum LineType implements Code<String> {
  CVC_SINGLE_LUMEN("CVC Single Lumen"),
  CVC_DOUBLE_LUMEN("CVC Double Lumen"),
  CVC_TRIPLE_LUMEN("CVC Triple Lumen"),
  CVC_QUADRUPLE_LUMEN("CVC Quadruple Lumen"),
  HEMODIALYSIS_PHERESIS_CATHETER("Hemodialysis/Pheresis Catheter"),
  INTRODUCER("Introducer"),
  MAC_INTRODUCER("MAC Introducer"),
  PSI_INTRODUCER("PSI Introducer"),
  PULMONARY_ARTERY_CATHETER("Pulmonary Artery Catheter"),
  PICC_SINGLE_LUMEN("PICC Single Lumen"),
  PICC_DOUBLE_LUMEN("PICC Double Lumen"),
  PICC_TRIPLE_LUMEN("PICC Triple Lumen"),
  IMPLANTED_PORT_SINGLE_LUMEN("Implanted Port Single Lumen"),
  IMPLANTED_PORT_DOUBLE_LUMEN("Implanted Port Double Lumen");

  private final String code;

  private static final CodeToEnumMapper<String, LineType> CODE_TO_ENUM_MAPPER =
      new CodeToEnumMapper<>(LineType.class);

  LineType(String code) {
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
  public static Optional<LineType> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
