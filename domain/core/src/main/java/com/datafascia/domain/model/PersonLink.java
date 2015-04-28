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
 * Link to a resource that concerns a Person.
 * Points to either a Patient, Practitioner or a RelatedPerson.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "PersonLink")
public class PersonLink {
  /** The resource to which this actual person is associated. */
  @JsonProperty("targetPatientId")
  private Id<Patient> targetPatientId;

  /** The resource to which this actual person is associated. */
  @JsonProperty("targetPractitionerId")
  private Id<Practitioner> targetPractitionerId;

  /** The resource to which this actual person is associated. */
  @JsonProperty("targetRelatedPersonId")
  private Id<RelatedPerson> targetRelatedPersonId;

  /** NIST Identity Assurance Level: Level1 | level2 | level3 | level4. */
  @JsonProperty("assurance")
  private PersonLinkAssurance assurance;
}
