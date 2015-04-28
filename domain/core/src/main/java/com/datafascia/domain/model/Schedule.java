// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Schedule model. Controls dates and times available for the performance
 * of a service and/or use of a resource.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Schedule")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_SCHEDULE_ID)
public class Schedule {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Schedule> id;

  /** Categorization of healthcare services or other appointment types. */
  @JsonProperty("types")
  private CodeableConcept types;

  /** The resource this schedule resource is providing availability information for. */
  @JsonProperty("actor")
  private Reference actor;

  /** The period of time for the slots in this Schedule Model. */
  @JsonProperty("planningHorizon")
  private Interval<Instant> planningHorizon;

  /** Comments on the availability to describe any extended information. */
  @JsonProperty("comment")
  private String comment;
}
