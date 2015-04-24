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
 * Precedent for a Condition. Must have either a code or a target.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ConditionOcurredFollowing")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ConditionOccurredFollowing {
  /** Relationship target by means of a predefined code. */
  @JsonProperty("code")
  private CodeableConcept code;

  /**
   * Relationship target resource, Condition | Procedure | MedicationAdministration | Immunization
   * | MedicationStatement.
   */
  @JsonProperty("target")
  private ConditionOccurredFollowingTarget target;
}
