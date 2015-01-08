// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Measurements and simple assertions made about a patient, device or other subject.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode(callSuper = true)
public class ReferenceRange extends Range {
  /** Code for the meaning of the reference range. */
  @JsonProperty("meaning")
  private CodeableConcept meaning;

  /**
   * The age at which this reference range is applicable. This is a neonatal age (e.g. number of
   * weeks at term) if the meaning says so.
   */
  @JsonProperty("age")
  private Range age;
}
