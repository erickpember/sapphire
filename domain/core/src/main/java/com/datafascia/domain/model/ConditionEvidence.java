// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URI;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Supporting evidence of a Condition.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ConditionEvidence")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ConditionEvidence {
  /** Manifestation/symptom. */
  @JsonProperty("code")
  private CodeableConcept code;

  /** Resource (Any) Supporting information found elsewhere. */
  @JsonProperty("details")
  private List<URI> details;
}
