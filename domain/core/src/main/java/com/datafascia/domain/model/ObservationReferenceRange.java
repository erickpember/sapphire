// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.reflect.AvroSchema;

/**
 * Measurements and simple assertions made about a patient, device or other subject.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "ObservationReferenceRange") @ToString(callSuper = true)
public class ObservationReferenceRange {
  /** Low range, if relevant. Low range comparator can not be > or >= or empty. */
  @JsonProperty("low")
  private NumericQuantity low;

  /** High range, if relevant. High range comparator can be < or <= or empty. */
  @JsonProperty("high")
  private NumericQuantity high;

  /** Code for the meaning of the reference range. */
  @JsonProperty("meaning")
  private CodeableConcept meaning;

  /**
   * The age at which this reference range is applicable. This is a neonatal age (e.g. number of
   * weeks at term) if the meaning says so.
   */
  @JsonProperty("age")
  private Range age;

  /** Text-based reference range in an observation. */
  @JsonProperty("text")
  private String text;
}
