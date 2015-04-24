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
 * Details for contact points for a person or organization, including telephone, email, etc.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ContactPoint")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ContactPoint {
  /** Phone | Fax | Email | URL. */
  @JsonProperty("system")
  private ContactPointSystem system;

  /** The actual contact point details. */
  @JsonProperty("value")
  private String value;

  /** Home | work | temp | old | mobile - purpose of this contact point. */
  @JsonProperty("use")
  private ContactPointUse use;

  /** Time period when the contact point was/is in use */
  @JsonProperty("period")
  private Interval<Instant> period;
}
