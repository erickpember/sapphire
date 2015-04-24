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
 * Represents people who performed the procedure. Can be of type Practitioner, Patient or
 * RelatedPerson
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ProcedurePerformer")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ProcedurePerformer {
  /** The reference to the performer. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** The reference to the performer. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** The reference to the performer. */
  @JsonProperty("relatedPersonId")
  private Id<RelatedPerson> relatedPersonId;

  /** The role the person was in. */
  @JsonProperty("role")
  private CodeableConcept role;
}
