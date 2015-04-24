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
 * Part of a Condition, references to Subject for a DiagnosticReport, only one instance of one
 * of these types:
 * Patient | Group | Device | Location
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DiagnosticSubject")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DiagnosticSubject {
  /** Subject for a DiagnosticReport. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Subject for a DiagnosticReport. */
  @JsonProperty("groupId")
  private Id<Group> groupId;

  /** Subject for a DiagnosticReport. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;

  /** Subject for a DiagnosticReport. */
  @JsonProperty("locationId")
  private Id<Location> locationId;
}
