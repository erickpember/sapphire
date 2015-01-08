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
 * Represents a value that is usually supplied by providing a reference to one or more terminologies
 * or ontologies, but may also be defined by the provision of text.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
public class CodeableConcept {
  // Coding Code defined by a terminology system.
  @JsonProperty("code")
  private String code;

  // Plain text representation of the concept.
  @JsonProperty("text")
  private String text;
}
