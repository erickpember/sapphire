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
 * Represents a Dosage Element as in MedicationStatement model.
 * This is an element that is a member of MedicationStatement.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "MedicationStatementDosage")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class MedicationStatementDosage {
  /** Dosage instructions. */
  @JsonProperty("text")
  private String text;

  /** When/ how often was medication taken? */
  @JsonProperty("schedule")
  private Schedule schedule;

  /** Take "as needed" f(or x). */
  @JsonProperty("asNeededBoolean")
  private Boolean asNeededBoolean;

  /** Take "as needed" f(or x). */
  @JsonProperty("asNeededCodeableConcept")
  private CodeableConcept asNeededCodeableConcept;

  /** Where on the body was the medication administered? */
  @JsonProperty("site")
  private CodeableConcept site;

  /** How did the medication enter the body? */
  @JsonProperty("route")
  private CodeableConcept route;

  /** Technique used to administer medication. */
  @JsonProperty("method")
  private CodeableConcept method;

  /** Amount administered in one dose. */
  @JsonProperty("quantity")
  private NumericQuantity quantity;

  /** Dose quantity per unit of time. */
  @JsonProperty("rate")
  private Ratio rate;

  /** Maximum dose that was consumed per unit of time. */
  @JsonProperty("maxDosePerPeriod")
  private Ratio maxDosePerPeriod;
}
