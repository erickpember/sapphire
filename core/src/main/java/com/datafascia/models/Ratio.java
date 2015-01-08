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
 * A relationship of two Quantity values - expressed as a numerator and a denominator.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
public class Ratio {
  // The value of the numerator.
  @JsonProperty("numerator")
  private Quantity numerator;

  // The value of the denominator.
  @JsonProperty("denominator")
  private Quantity denominator;
}
