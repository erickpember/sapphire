// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
