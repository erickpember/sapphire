// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.net.URI;
import javax.measure.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Implements the FHIR data type which represents an amount that is measured or can potentially
 * be measured. This model is used in most places where a numeric amount is paired with units
 * of measure.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "NumericQuantity")
public final class NumericQuantity implements Comparable<NumericQuantity> {
  /** The value to hold. */
  @JsonProperty("value")
  private BigDecimal value;

  /** The unit being measured. */
  @JsonProperty("unit")
  private Unit unit;

  /** How to understand this value: greater | less | greater_or_equal | less_or_equal. */
  @JsonProperty("comparator")
  private Comparator comparator;

  /** System that defines the coded unit form. */
  @JsonProperty("system")
  private URI system;

  /** Coded form of the unit. */
  @JsonProperty("code")
  private String code;

  /**
   * A container that associates a number with a unit.
   *
   * @param value Number to contain.
   * @param unit Unit to contain.
   */
  public NumericQuantity(BigDecimal value, Unit unit) {
    this(value, unit, null, null);
  }

  /**
   * A container that associates a number with a unit.
   *
   * @param value Number to contain.
   * @param unit Unit to contain.
   * @param system URI to system that defines the code.
   * @param code String that is a coded form of the quantity.
   */
  public NumericQuantity(BigDecimal value, Unit unit, URI system, String code) {
    this.value = value;
    this.unit = unit;
    this.system = system;
    this.code = code;
  }

  @Override
  public int compareTo(NumericQuantity num) {
    return value.compareTo(num.getValue());
  }
}
