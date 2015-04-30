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
import lombok.extern.slf4j.Slf4j;

/**
 * A relationship of two Quantity values - expressed as a numerator and a denominator.
 */
@AllArgsConstructor @Data @Slf4j @NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Ratio")
public class Ratio {
  /** The value of the numerator.*/
  @JsonProperty("numerator")
  private NumericQuantity numerator;

  /** The value of the denominator.*/
  @JsonProperty("denominator")
  private NumericQuantity denominator;
}
