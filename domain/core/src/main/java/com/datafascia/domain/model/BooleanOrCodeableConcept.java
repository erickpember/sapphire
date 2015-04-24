// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents cases in FHIR schema specified as "boolean|CodeableConcept", for uses
 * where one or the other is suitable, with preference for the CodeableConcept over
 * the boolean value.
 */
@AllArgsConstructor @Data @NoArgsConstructor @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "BooleanOrCodeableConcept")
public class BooleanOrCodeableConcept {
  /** Codeable Concept alternative to boolean. */
  @JsonProperty("code")
  private CodeableConcept code;

  /** Boolean alternative to the CodeableConcept. */
  @JsonProperty("bool")
  private Boolean bool;
}
