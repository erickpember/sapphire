// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Part of Observation model. Represents who or what or where an Observation is about.
 * Refers to a Model of type Patient, Group, Device or Location.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "ObservationSubject")
public class ObservationSubject {
  /** Who the observation is about. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Who/what the observation is about. */
  @JsonProperty("groupId")
  private Id<Group> groupId;

  /** What the observation is about. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;

  /** Where the observation is about. */
  @JsonProperty("locationId")
  private Id<Location> locationId;
}
