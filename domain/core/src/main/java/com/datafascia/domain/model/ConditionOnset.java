// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Time of onset for a Condition. One of many types can represent the time.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ConditionOnset")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ConditionOnset {
  /** Date/Time of onset. */
  @JsonProperty("onsetDateTime") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant onsetDateTime;

  /** Age of onset. */
  @JsonProperty("onsetAge")
  private NumericQuantity onsetAge;

  /** Time range of onset. */
  @JsonProperty("onsetPeriod")
  private Interval<Instant> onsetPeriod;

  /** Numerical range of onset. */
  @JsonProperty("onsetRange")
  private Range onsetRange;

  /** Text representation of onset. */
  @JsonProperty("onsetString")
  private String onsetString;
}
