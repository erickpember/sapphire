// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a reason or indication for writing a Prescription. Contains either an instance of
 * CodableConcept or Condition Id.
 */
@AllArgsConstructor @Data @NoArgsConstructor @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "MedicationPrescriptionReason")
public class MedicationPrescriptionReason {
  /** Codeable Concept alternative to Condition Id. */
  @JsonProperty("code")
  private CodeableConcept code;

  /** Condition alternative to the CodeableConcept. */
  @JsonProperty("conditionId")
  private Id<Condition> conditionId;
}
