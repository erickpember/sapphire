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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Specifies an event that may occur multiple times.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Timing")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class Timing {
  /** When the event occurs. */
  @JsonProperty("events")
  private List<Instant> events;

  /** Start and/or end limits. */
  @JsonProperty("repeatBounds")
  private Interval<Instant> repeatBounds;

  /** Number of times to repeat. */
  @JsonProperty("repeatCount")
  private Integer repeatCount;

  /** Repeating or event-related duration. */
  @JsonProperty("repeatDuration")
  private BigDecimal repeatDuration;

  /** Either s | min | h | d | wk | mo | a - unit of time (UCUM). */
  @JsonProperty("repeatDurationUnits")
  private TimingTimeUnit repeatDurationUnits;

  /** Event occurs frequency times per duration. */
  @JsonProperty("repeatFrequency")
  private BigDecimal repeatFrequency;

  /** Event occurs frequency times per duration. */
  @JsonProperty("repeatFrequencyMax")
  private BigDecimal repeatFrequencyMax;

  /** Event occurs frequency times per period. */
  @JsonProperty("repeatPeriod")
  private BigDecimal repeatPeriod;

  /** Upper limit of period (3-4 hours). */
  @JsonProperty("repeatPeriodMax")
  private BigDecimal repeatPeriodMax;

  /** Either s | min | h | d | wk | mo | a - unit of time (UCUM). */
  @JsonProperty("repeatPeriodUnits")
  private TimingTimeUnit repeatPeriodUnits;

  /** HS | WAKE | AC | ACM | ACD | ACV | PC | PCM | PCD | PCV - common life events. */
  @JsonProperty("repeatWhen")
  private TimingEventType repeatWhen;

  /** Either s | min | h | d | wk | mo | a - unit of time (UCUM). */
  @JsonProperty("repeatUnits")
  private TimingTimeUnit repeatUnits;

  /** When to stop repeats. */
  @JsonProperty("repeatEnd") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant repeatEnd;
}
