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
 * Part of a DiagnosticReport, contains one piece of supporting information
 * of one of these types:
 * Observation | Condition | DocumentReference
 */
@Data @NoArgsConstructor
@JsonTypeName(URNFactory.MODEL_PREFIX + "DiagnosticOrderSupportingInformation")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DiagnosticOrderSupportingInformation {
  /** Additional clinical information. */
  @JsonProperty("observationId")
  private Id<Observation> observationId;

  /** Additional clinical information. */
  @JsonProperty("conditionId")
  private Id<Condition> conditionId;

  /** Additional clinical information. */
  @JsonProperty("documentReferenceId")
  private Id<DocumentReference> documentReferenceId;
}
