// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A time that a HealthCare service site is not available.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "HealthcareServiceNotAvailable")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class HealthcareServiceNotAvailable {
  /** THe reason presented to the user as to why this time is not available. */
  @JsonProperty("description")
  private String description;

  /** Service is not available from this date. */
  @JsonProperty("during")
  private Interval<Instant> during;
}
