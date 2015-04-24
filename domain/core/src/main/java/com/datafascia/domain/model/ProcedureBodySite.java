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
 * Represents a ProcedureBodySite element, part of Procedure Model, detailing precise
 * location details for the procedure.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ProcedureBodySite")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ProcedureBodySite {
  /** Precise location details. */
  @JsonProperty("siteCodeableConcept")
  private CodeableConcept siteCodeableConcept;

  /** Precise location details. */
  @JsonProperty("siteReferenceId")
  private Id<BodySite> siteReferenceId;
}
