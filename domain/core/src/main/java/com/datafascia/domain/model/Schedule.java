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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Specifies an event that may occur multiple times.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Schedule")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class Schedule {
  /** Period when the event occurs. */
  @JsonProperty("event")
  private Interval<Instant> event;

  /** Relationship target by means of a predefined code. */
  @JsonProperty("repeatFrequency")
  private Integer repeatFrequency;

  /** HS | WAKE | AC | ACM | ACD | ACV | PC | PCM | PCD | PCV - common life events. */
  @JsonProperty("repeatWhen")
  private ScheduleEventType repeatWhen;

  /** Repeating or event-related duration. */
  @JsonProperty("repeatDuration")
  private BigDecimal repeatDuration;

  /** Either s | min | h | d | wk | mo | a - unit of time (UCUM). */
  @JsonProperty("repeatUnits")
  private ScheduleTimeUnit repeatUnits;

  /** Number of times to repeat. */
  @JsonProperty("repeatCount")
  private Integer repeatCount;

  /** When to stop repeats. */
  @JsonProperty("repeatEnd") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant repeatEnd;
}
