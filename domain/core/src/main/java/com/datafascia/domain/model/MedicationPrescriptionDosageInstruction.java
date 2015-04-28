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
 * How medication should be taken.
 */
@Data @NoArgsConstructor
@JsonTypeName(URNFactory.MODEL_PREFIX + "MedicationPrescriptionDosageInstruction")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class MedicationPrescriptionDosageInstruction {
  /** Dosage instructions expressed as text. */
  @JsonProperty("text")
  private String text;

  /** Supplemental instructions - e.g. "with meals". */
  @JsonProperty("additionalInstructions")
  private CodeableConcept additionalInstructions;

  /** When medication should be administered. */
  @JsonProperty("timing")
  private Timing timing;

  /** Boolean | CodeableConcept, Take "as needed". */
  @JsonProperty("asNeeded")
  private BooleanOrCodeableConcept asNeeded;

  /** Body site to administer to. */
  @JsonProperty("site")
  private CodeableConcept site;

  /** How drug should enter body. */
  @JsonProperty("route")
  private CodeableConcept route;

  /** Technique for administering medication. */
  @JsonProperty("method")
  private CodeableConcept method;

  /** Range of medication per dose. */
  @JsonProperty("doseRange")
  private Range doseRange;

  /** Amount of medication per dose. */
  @JsonProperty("doseQuantity")
  private NumericQuantity doseQuantity;

  /** Amount of medication per unit of time. */
  @JsonProperty("rate")
  private Ratio rate;

  /** Upper limit on medication per unit of time. */
  @JsonProperty("maxDosePerPeriod")
  private Ratio maxDosePerPeriod;
}
