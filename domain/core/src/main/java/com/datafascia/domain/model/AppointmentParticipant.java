// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a AppointmentParticipant model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "AppointmentParticipant")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class AppointmentParticipant {
  /** Role of participant in the appointment. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** A person, location, healthcare service, or device that is participating in the appointment. */
  @JsonProperty("actor")
  private AppointmentParticipantActor actor;

  /** Required | optional | information_only. */
  @JsonProperty("required")
  private AppointmentParticipantRequired required;

  /** Accepted | declined | tentative | needs_action. */
  @JsonProperty("status")
  private AppointmentParticipantStatus status;
}
