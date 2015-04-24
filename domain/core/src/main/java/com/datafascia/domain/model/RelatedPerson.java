// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
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
 * Represents a contact relating a person to a patient.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "RelatedPerson") @ToString(callSuper = true)
@IdNamespace(URNFactory.NS_RELATED_PERSON_ID)
public class RelatedPerson extends Person {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<RelatedPerson> id;

  /** Relationship of the contact to the person. */
  @JsonProperty("relationship")
  private CodeableConcept relationship;

  /** The patient to whom this person is related. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;
}
