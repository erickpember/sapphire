// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

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
 * An Element of the Patient model that holds animal-specific information in the
 * event a Patient is a non-human animal.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "PatientAnimal")
public class PatientAnimal {
  /** E.g. Dog, Platypus. */
  @JsonProperty("species")
  private CodeableConcept species;

  /** E.g. Labradoodle, Holstein. */
  @JsonProperty("breed")
  private CodeableConcept breed;

  /** E.g. Neutered, Intact. */
  @JsonProperty("genderStatus")
  private CodeableConcept genderStatus;
}
