// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * A measured amount (or an amount that can potentially be measured). Note that measured amounts
 * include amounts that are not precisely quantified, including amounts involving arbitrary units
 * and floating currencies.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("Quantity")
public class Quantity {
  /**
   * The value of the measured amount. The value includes an implicit precision in the presentation
   * of the value.
   */
  @JsonProperty("value")
  private BigDecimal value;

  /**
   * How the value should be understood and represented - whether the actual value is greater or
   * less than the stated value due to measurement issues. E.g. if the comparator is "&lt;" , then
   * the real value is &lt; stated value.
   */
  @JsonProperty("Comparator")
  private QuantityComparator comparator;

  /** A human-readable form of the units. */
  @JsonProperty("units")
  private String units;

  /** The identification of the system that provides the coded form of the unit. */
  @JsonProperty("system")
  private URI system;

  /** A computer processable form of the units in some unit representation system. */
  @JsonProperty("code")
  private CodeableConcept code;
}
