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
 * Stage/grade of a Condition, usually assessed formally.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ConditionStage")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ConditionStage {
  /** Simple summary (disease specific). */
  @JsonProperty("summary")
  private CodeableConcept summary;

  /** Resource (Any) Formal record of assessment. */
  @JsonProperty("assessments")
  private List<URI> assessments;
}
