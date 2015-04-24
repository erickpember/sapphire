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
 * Part of a Condition, references to precedent for the Condition, only one instance of one
 * of these types:
 * ClinicalImpression | DiagnosticReport | Observation
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ConditionAssessment")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ConditionAssessment {
  /** Precedent for the condition. */
  @JsonProperty("conditionId")
  private Id<Condition> conditionId;

  /** Precedent for the condition. */
  @JsonProperty("procedureId")
  private Id<Procedure> procedureId;

  /** Precedent for the condition. */
  @JsonProperty("medicationAdministrationId")
  private Id<MedicationAdministration> medicationAdministrationId;

  /** Precedent for the condition. */
  @JsonProperty("immunizationId")
  private Id<Immunization> immunizationId;

  /** Precedent for the condition. */
  @JsonProperty("medicationStatementId")
  private Id<MedicationStatement> medicationStatementId;
}
