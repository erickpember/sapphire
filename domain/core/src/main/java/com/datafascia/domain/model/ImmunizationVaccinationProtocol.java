// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a vaccinationProtocol Element as part of Immunization model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ImmunizationVaccinationProtocol")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ImmunizationVaccinationProtocol {
  /** What dose number within series? */
  @JsonProperty("doseSequence")
  private BigDecimal doseSequence;

  /** Details of vaccine protocol. */
  @JsonProperty("description")
  private String description;

  /** Who is responsible for the protocol. */
  @JsonProperty("authorityId")
  private Id<Organization> authorityId;

  /** Name of vaccine series. */
  @JsonProperty("series")
  private String series;

  /** Recommended number of doses for immunity. */
  @JsonProperty("seriesDoses")
  private BigDecimal seriesDoses;

  /** Disease immunized against. */
  @JsonProperty("doseTarget")
  private CodeableConcept doseTarget;

  /** Does dose count towards immunity? */
  @JsonProperty("doseStatus")
  private CodeableConcept doseStatus;

  /** Why does dose count or not count? */
  @JsonProperty("doseStatusReason")
  private CodeableConcept doseStatusReason;
}
