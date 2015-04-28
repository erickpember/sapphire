// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents the Target Element of the Order Model, which serves as a reference
 * to who is intended to fulfill the order.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "OrderTarget")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class OrderTarget {
  /** Who is intended to fulfill the order. */
  @JsonProperty("organizationId")
  private Id<Organization> organizationId;

  /** Who is intended to fulfill the order. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** Who is intended to fulfill the order. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;
}
