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
