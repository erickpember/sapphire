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
 * Where the specimen came from, only one instance of one
 * of these types:
 * Patient | Substance | Group | Device
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "SpecimenSubject")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class SpecimenSubject {
  /** Origin of the specimen. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Origin of the specimen. */
  @JsonProperty("substanceId")
  private Id<Substance> substanceId;

  /** Origin of the specimen. */
  @JsonProperty("groupId")
  private Id<Group> groupId;

  /** Origin of the specimen. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;
}
