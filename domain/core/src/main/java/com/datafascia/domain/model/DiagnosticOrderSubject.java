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
 * Represents one subject of a DiagnosticOrder, of one type, either
 * Patient, Group, Location, or Device.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DiagnosticOrderSubject")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DiagnosticOrderSubject {
  /** Who and/or what the test is about. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Who and/or what the test is about. */
  @JsonProperty("groupId")
  private Id<Group> groupId;

  /** Who and/or what the test is about. */
  @JsonProperty("locationId")
  private Id<Location> locationId;

  /** Who and/or what the test is about. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;
}
