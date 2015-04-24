// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.DurationDeserializer;
import com.datafascia.common.jackson.DurationSerializer;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.DayOfWeek;
import java.time.Duration;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A time that a HealthcareService site is available.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "HealthcareServiceAvailableTime")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class HealthcareServiceAvailableTime {
  /** Days of week available. */
  @JsonProperty("daysOfWeek")
  private List<DayOfWeek> daysOfWeek;

  /** Is this available all day? E.g. 24 hour service. */
  @JsonProperty("allDay")
  private Boolean allDay;

  /** Opening time of day. Stored as duration since midnight. */
  @JsonProperty("availableStartTime") @JsonSerialize(using = DurationSerializer.class)
  @JsonDeserialize(using = DurationDeserializer.class)
  private Duration availableStartTime;

  /** Closing time of day. Stored as duration since midnight. */
  @JsonProperty("availableEndTime") @JsonSerialize(using = DurationSerializer.class)
  @JsonDeserialize(using = DurationDeserializer.class)
  private Duration availableEndTime;
}
