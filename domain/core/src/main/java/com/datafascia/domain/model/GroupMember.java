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
 * Part of a Group, refers to a member of one of these types:
 * Patient, Practitioner, Device, Medication, Substance
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "GroupMember")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class GroupMember {
  /** Member of a Group. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Member of a Group. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** Member of a Group. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;

  /** Member of a Group. */
  @JsonProperty("medicationId")
  private Id<Medication> medicationId;

  /** Member of a Group. */
  @JsonProperty("substanceId")
  private Id<Substance> substanceId;
}
