// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import lombok.Getter;

/**
 * PatientUpdateMap field constants.
 */
@Getter
public enum UpdateField {

  CAPTURE_TIME("CaptureTime", "Capture Time"),
  NUMERICAL_PAIN_LEVEL_LOW("dF_numericalPainLevelLow", "Numerical Pain Level (Low)"),
  NUMERICAL_PAIN_LEVEL_HIGH("dF_numericalPainLevelHigh", "Numerical Pain Level (High)"),
  BEHAVIORAL_PAIN_LEVEL_LOW("dF_behavioralPainLevelLow", "Behavior Pain Level (Low)"),
  BEHAVIORAL_PAIN_LEVEL_HIGH("dF_behavioralPainLevelHigh", "Behavior Pain Level (High)"),
  RASS_PAIN_LEVEL_LOW("dF_rassPainLevelLow", "RASS Level (Low)"),
  RASS_PAIN_LEVEL_HIGH("dF_rassPainLevelHigh", "RASS Level (High)");

  private final String fieldName;
  private final String displayName;

  private static final Map<String, UpdateField> fieldNameToValueMap = new HashMap<>();

  static {
    for (UpdateField value : UpdateField.values()) {
      fieldNameToValueMap.put(value.getFieldName(), value);
    }
  }

  private UpdateField(String fieldName, String displayName) {
    this.fieldName = fieldName;
    this.displayName = displayName;
  }

  /**
   * Converts field name to enum value.
   *
   * @param fieldName
   *     to convert
   * @return optional enum value, empty if field name is unknown
   */
  public static Optional<UpdateField> ofFieldName(String fieldName) {
    return Optional.ofNullable(fieldNameToValueMap.get(fieldName));
  }
}
