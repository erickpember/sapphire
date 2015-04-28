// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Slot model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Slot")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_SLOT_ID)
public class Slot {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Slot> id;

  /** The type of appointments that can be booked into this slot. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** The schedule resource that this slot defines. */
  @JsonProperty("schedule")
  private Id<Schedule> schedule;

  /** Busy | free | busy_unavailable | busy_tentative. */
  @JsonProperty("freeBusyType")
  private SlotFreeBusyType freeBusyType;

  /** Date/Time that the slot is to begin. */
  @JsonProperty("start") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant start;

  /** Date/Time that the slot is to conclude. */
  @JsonProperty("end") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant end;

  /** This slot has already been over-booked, appointments are unlikely to be accepted. */
  @JsonProperty("overbooked")
  private Boolean overbooked;

  /** Comments on the slot to describe any such needed info, such as custom constraints. */
  @JsonProperty("comment")
  private String comment;
}
