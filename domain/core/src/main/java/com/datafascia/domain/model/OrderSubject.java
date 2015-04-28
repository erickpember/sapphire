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
 * Represents the subject Element of the Order model, a reference to
 * what an Order is about, of type Patient, Group, Device, Substance.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "OrderSubject")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class OrderSubject {
  /** Patient this order is about. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Group this order is about. */
  @JsonProperty("groupId")
  private Id<Group> groupId;

  /** Device this order is about. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;

  /** Substance this order is about. */
  @JsonProperty("substanceId")
  private Id<Substance> substanceId;
}
