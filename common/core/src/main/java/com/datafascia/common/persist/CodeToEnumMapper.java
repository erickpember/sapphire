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
package com.datafascia.common.persist;

import java.util.HashMap;
import java.util.Optional;

/**
 * Converts code to enum constant.
 *
 * @param <C>
 *     code representation type
 * @param <E>
 *     enum type, must implement {@link Code} interface
 */
public class CodeToEnumMapper<C, E extends Enum<E>> {

  private HashMap<C, E> codeToEnumMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param enumClass
   *     enum class
   */
  public CodeToEnumMapper(Class<E> enumClass) {
    for (E e : enumClass.getEnumConstants()) {
      C code = ((Code<C>) e).getCode();
      if (codeToEnumMap.containsKey(code)) {
        throw new IllegalArgumentException(String.format(
            "Mapping to enum constant [%s] already exists while trying to map code [%s]",
            e,
            code));
      }

      codeToEnumMap.put(code, e);
    }
  }

  /**
   * Converts code to enum constant.
   *
   * @param code
   *     input code
   * @return optional enum constant, empty if code is unknown
   */
  public Optional<E> of(C code) {
    return Optional.ofNullable(codeToEnumMap.get(code));
  }
}
