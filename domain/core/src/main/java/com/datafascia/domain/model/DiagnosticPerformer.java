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
 * Represents a performer of a DiagnosticReport, either of type Practitioner or Organization
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DiagnosticPerformer")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DiagnosticPerformer {
  /** Responsible Diagnostic service. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** Responsible Diagnostic service. */
  @JsonProperty("organizationId")
  private Id<Organization> organizationId;
}
