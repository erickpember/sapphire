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
 * This represents a single cause of a Condition that can be only one of these types:
 * Condition | Procedure | MedicationAdministration | Immunization | MedicationStatement.
 */
@AllArgsConstructor @Data @NoArgsConstructor @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "ConditionDueToTarget")
public class ConditionDueToTarget {
  /** Cause of a condition. */
  @JsonProperty("ConditionId")
  private Id<Condition> ConditionId;

  /** Cause of a condition. */
  @JsonProperty("ProcedureId")
  private Id<Procedure> ProcedureId;

  /** Cause of a condition. */
  @JsonProperty("MedicationAdministrationId")
  private Id<MedicationAdministration> MedicationAdministrationId;

  /** Cause of a condition. */
  @JsonProperty("ImmunizationId")
  private Id<Immunization> ImmunizationId;

  /** Cause of a condition. */
  @JsonProperty("MedicationStatementId")
  private Id<MedicationStatement> MedicationStatementId;
}
