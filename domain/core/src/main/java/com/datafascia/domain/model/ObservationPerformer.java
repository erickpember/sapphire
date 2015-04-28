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
 * Part of Observation Model: who performed the observation?
 * Serves as a reference to either a Practitioner, an Organization, or a Patient.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "ObservationPerformer")
public class ObservationPerformer {
  /** Who performed the observation? */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** What performed the observation? */
  @JsonProperty("organizationId")
  private Id<Organization> organizationId;

  /** Who performed the observation? */
  @JsonProperty("patientId")
  private Id<Patient> patientId;
}
