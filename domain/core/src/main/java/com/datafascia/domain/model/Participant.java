// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Identifies a participant in an Encounter. Either of type Practitioner or RelatedPerson.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Participant")
public class Participant {
  /** Role of participant in encounter. */
  @JsonProperty("types")
  private List<CodeableConcept> types;

  /** Period of time during the encounter when the participant was present. */
  @JsonProperty("period")
  private Interval<Instant> period;

  /** Persons involved in the encounter other than the patient. */
  @JsonProperty("individualPractitionerId")
  private Id<Practitioner> individualPractitionerId;

  /** Persons involved in the encounter other than the patient. */
  @JsonProperty("individualRelatedPersonId")
  private Id<RelatedPerson> individualRelatedPersonId;
}
