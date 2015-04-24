// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents qualifications obtained by training and certification.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Qualification")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class Qualification {
  /** Coded representation of the qualification */
  @JsonProperty("code")
  private CodeableConcept code;

  /** Period during which the qualification is valid. */
  @JsonProperty("period")
  private Interval<Instant> period;

  /** Organization that regulates and issues the qualification. */
  @JsonProperty("issuerId")
  private Id<Organization> issuerId;
}
