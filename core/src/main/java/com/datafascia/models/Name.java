// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a person's name, with a first, middle, and last name.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
public class Name {
  @JsonProperty("first")
  private String first;
  @JsonProperty("middle")
  private String middle;
  @JsonProperty("last")
  private String last;
}
