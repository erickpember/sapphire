// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.LocalDateDeserializer;
import com.datafascia.common.jackson.LocalDateSerializer;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import java.time.LocalDate;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Time of abatement of a Condition. One of many types can represent the time.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ConditionAbatement")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ConditionAbatement {
  /** Date of abatement. */
  @JsonProperty("abatementDate") @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate abatementDate;

  /** Age of abatement. */
  @JsonProperty("abatementAge")
  private NumericQuantity abatementAge;

  /** False if abatement has not occurred. */
  @JsonProperty("abatementBoolean")
  private Boolean abatementBoolean;

  /** Time range of onset. */
  @JsonProperty("abatementPeriod")
  private Interval<Instant> abatementPeriod;

  /** Numerical range of abatement. */
  @JsonProperty("abatementRange")
  private Range abatementRange;

  /** Text representation of abatement. */
  @JsonProperty("abatementString")
  private String abatementString;
}
