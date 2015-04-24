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
 * Who recorded or did an Event in a DiagnosticOrder
 * of these types:
 * Practitioner | Device
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DiagnosticOrderEventActor")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DiagnosticOrderEventActor {
  /** Who recorded or did an Event. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** Who recorded or did an Event. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;
}
