// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
