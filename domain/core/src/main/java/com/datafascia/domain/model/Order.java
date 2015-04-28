// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an Order model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Order")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_ORDER_ID)
public class Order {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Order> id;

  /** WHen the order was made. */
  @JsonProperty("dateTime")
  private Instant dateTime;

  /** Patient this order is about. */
  @JsonProperty("subject")
  private OrderSubject subject;

  /** Who initiated the order. */
  @JsonProperty("sourceId")
  private Id<Practitioner> sourceId;

  /** Who is intended to fulfill the order. */
  @JsonProperty("target")
  private OrderTarget target;

  /** Why the order was made. */
  @JsonProperty("reasonCodeableConcept")
  private CodeableConcept reasonCodeableConcept;

  /** Why the order was made. */
  @JsonProperty("reasonReference")
  private Reference reasonReference;

  /** If required by policy. */
  @JsonProperty("authority")
  private Reference authority;

  /** When order should be fulfilled. */
  @JsonProperty("when")
  private OrderWhen when;

  /** What action is being ordered. */
  @JsonProperty("details")
  private List<Reference> details;
}
