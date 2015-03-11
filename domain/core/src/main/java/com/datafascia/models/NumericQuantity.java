// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact

package com.datafascia.models;

import com.datafascia.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import javax.measure.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "NumericQuantity")
public final class NumericQuantity implements Comparable<NumericQuantity> {
  /** The value to hold, in BigDecimal format. */
  @JsonProperty("value")
  private BigDecimal value;

  /** The unit being measured. */
  @JsonProperty("unit")
  private Unit unit;

  /**
   * A container that associates a number with a unit.
   * @param value Number to contain.
   * @param unit Unit to contain.
   */
  public NumericQuantity(BigDecimal value, Unit unit) {
    this.value = value;
    this.unit = unit;
  }

  @Override
  public int compareTo(NumericQuantity num) {
    return value.compareTo(num.getValue());
  }
}
