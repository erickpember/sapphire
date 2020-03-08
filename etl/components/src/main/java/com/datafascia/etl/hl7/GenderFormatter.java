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
package com.datafascia.etl.hl7;

import ca.uhn.fhir.model.dstu2.valueset.AdministrativeGenderEnum;
import com.google.common.collect.ImmutableMap;
import java.util.Map;

/**
 * Converts between gender codes and gender enum constants.
 */
public class GenderFormatter {

  private static final Map<String, AdministrativeGenderEnum> codeToGenderMap = ImmutableMap.of(
      "F", AdministrativeGenderEnum.FEMALE,
      "M", AdministrativeGenderEnum.MALE,
      "O", AdministrativeGenderEnum.OTHER);

  // Private constructor disallows creating instances of this class.
  private GenderFormatter() {
  }

  /**
   * Converts gender code to gender.
   * <p>
   * Case-insensitively maps the first letter of the gender code to a gender as follows:
   * <dl>
   *   <dt>{@code F}
   *   <dd>FEMALE
   *   <dt>{@code M}
   *   <dd>MALE
   *   <dt>{@code O}
   *   <dd>OTHER
   *   <dt>anything else
   *   <dd>UNKNOWN
   * </dl>
   *
   * @param genderCode
   *     to convert
   * @return gender
   */
  public static AdministrativeGenderEnum parse(String genderCode) {
    String normalizedCode = genderCode.trim().toUpperCase();
    if (normalizedCode.length() > 0) {
      normalizedCode = normalizedCode.substring(0, 1);
    }

    return codeToGenderMap.getOrDefault(normalizedCode, AdministrativeGenderEnum.UNKNOWN);
  }
}
