// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Characteristic Element from the Group model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "GroupCharacteristic")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class GroupCharacteristic {
  /** Kind of characteristic. */
  @JsonProperty("code")
  private CodeableConcept code;

  /** Value held by characteristic. */
  @JsonProperty("valueCodeableConcept")
  private CodeableConcept valueCodeableConcept;

  /** Value held by characteristic. */
  @JsonProperty("valueBoolean")
  private Boolean valueBoolean;

  /** Value held by characteristic. */
  @JsonProperty("valueQuantity")
  private NumericQuantity valueQuantity;

  /** Value held by characteristic. */
  @JsonProperty("valueRange")
  private Range valueRange;

  /** Group includes or excludes. */
  @JsonProperty("exclude")
  private Boolean exclude;
}
