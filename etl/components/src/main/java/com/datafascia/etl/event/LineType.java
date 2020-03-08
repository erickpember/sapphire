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
package com.datafascia.etl.event;

import com.datafascia.common.persist.Code;
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
    for (LineType lineType : values()) {
      if (code.startsWith(lineType.getCode())) {
        return Optional.of(lineType);
      }
    }

    return Optional.empty();
  }
}
