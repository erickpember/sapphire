// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a value that is usually supplied by providing a reference to one or more terminologies
 * or ontologies, but may also be defined by the provision of text.
 */
@AllArgsConstructor @Data @NoArgsConstructor @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "CodeableConcept")
public class CodeableConcept {
  /** Coding Code defined by a terminology system.*/
  @JsonProperty("code")
  private String code;

  /** Plain text representation of the concept.*/
  @JsonProperty("text")
  private String text;
}
