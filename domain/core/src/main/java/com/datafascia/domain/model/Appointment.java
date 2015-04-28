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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an Appointment model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Appointment")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_APPOINTMENT_ID)
public class Appointment {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Appointment> id;

  /** Pending | booked | arrived | fulfilled | cancelled | noshow. */
  @JsonProperty("status")
  private AppointmentStatus status;

  /** The type of appointment that is being booked. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** The reason that this appointment is being scheduled, more clinical than administrative. */
  @JsonProperty("reason")
  private CodeableConcept reason;

  /** The priority of the appointment. Can be used to make informed reprioritization decisions. */
  @JsonProperty("priority")
  private BigDecimal priority;

  /** The description of the appointment, such as in the subject line in a meeting request. */
  @JsonProperty("description")
  private String description;

  /** Date/Time that the appointment is to take place. */
  @JsonProperty("start") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant start;

  /** Date/Time that the appointment is to conclude. */
  @JsonProperty("end") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant end;

  /** The slot this appointment is filling. */
  @JsonProperty("slotIds")
  private List<Id<Slot>> slotIds;

  /** Additional comments about the appointment. */
  @JsonProperty("comment")
  private String comment;

  /** An Order that lead to the creation of this appointment. */
  @JsonProperty("orderId")
  private Id<Order> orderId;

  /** List of participants. */
  @JsonProperty("participants")
  private List<AppointmentParticipant> participants;
}
