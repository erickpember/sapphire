// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an event Element as part of a DiagnosticOrder Model, which itself
 * represents a list of events of interest in the lifecycle.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DiagnosticOrderEvent")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DiagnosticOrderEvent {
  /**
   * Proposed | draft | planned | requested | received | accepted | in-progress | review |
   * completed | cancelled | suspended | rejected | failed.
   */
  @JsonProperty("status")
  private DiagnosticOrderEventStatus status;

  /** More information about the event and its context. */
  @JsonProperty("description")
  private CodeableConcept description;

  /** THe date at which the event happened. */
  @JsonProperty("dateTime") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant dateTime;

  /** Who recorded or did this, of type Practitioner or Device. */
  @JsonProperty("actor")
  private DiagnosticOrderEventActor actor;
}
