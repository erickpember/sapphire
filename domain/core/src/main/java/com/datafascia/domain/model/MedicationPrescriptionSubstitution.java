// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a substitution used in a MedicationPrescription.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX
    + "MedicationPrescriptionSubstitution")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class MedicationPrescriptionSubstitution {
  /** "generic | formulary + MedicationIntendedSubstitutionType" */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Why should substitution (not) be made. */
  @JsonProperty("reason")
  private CodeableConcept reason;
}
