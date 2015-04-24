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
 * Details on the dosage in a MedicationAdministration Model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "MedicationAdministrationDosage")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class MedicationAdministrationDosage {
  /** Dosage Instructions */
  @JsonProperty("text")
  private String text;

  /** Body site to which the medication was administered. */
  @JsonProperty("site")
  private CodeableConcept site;

  /** Path of substance into body. */
  @JsonProperty("route")
  private CodeableConcept route;

  /** How the drug was administered. */
  @JsonProperty("method")
  private CodeableConcept method;

  /** Amount administered in one dose. */
  @JsonProperty("quantity")
  private NumericQuantity quantity;

  /** Dose quantity per unit of time. */
  @JsonProperty("rate")
  private Ratio rate;
}
