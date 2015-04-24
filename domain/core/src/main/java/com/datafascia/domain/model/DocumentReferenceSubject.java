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
 * Who|what is the subject of a DocumentReference, only one instance of one
 * of these types:
 * Patient | Practitioner | Group | Device
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DocumentReferenceSubject")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DocumentReferenceSubject {
  /** Subject of a DocumentReference. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Subject of a DocumentReference. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** Subject of a DocumentReference. */
  @JsonProperty("groupId")
  private Id<Group> groupId;

  /** Subject of a DocumentReference. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;
}
