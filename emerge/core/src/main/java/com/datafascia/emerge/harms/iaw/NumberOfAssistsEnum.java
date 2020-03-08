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
package com.datafascia.emerge.harms.iaw;

import com.datafascia.common.persist.CodeToEnumMapper;
import com.datafascia.domain.fhir.SystemDefinedCode;
import java.util.Optional;

/**
 * Enumerates possible values for "number of assists" in the IAW mobility score.
 */
public enum NumberOfAssistsEnum implements SystemDefinedCode<String> {
  NOT_DOCUMENTED("Not Documented"),
  NONE("None"),
  ONE("1"),
  TWO("2"),
  THREE_PLUS("3+");

  private static final String SYSTEM = "http://datafascia.com/code/NumberOfAssists";

  private final String code;

  private static final CodeToEnumMapper<String, NumberOfAssistsEnum> CODE_TO_ENUM_MAPPER
      = new CodeToEnumMapper<>(NumberOfAssistsEnum.class);

  NumberOfAssistsEnum(String code) {
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
  public static Optional<NumberOfAssistsEnum> of(String code) {
    return CODE_TO_ENUM_MAPPER.of(code);
  }
}
