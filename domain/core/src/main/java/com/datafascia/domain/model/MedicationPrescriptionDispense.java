// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Medication supply authorization. Part of MedicationPrescription model, this is not the
 * MedicationDispense model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "MedicationPrescriptionDispense")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class MedicationPrescriptionDispense {
  /** Product to be supplied. */
  @JsonProperty("medicationId")
  private Id<Medication> medicationId;

  /** Time period supply is authorized for. */
  @JsonProperty("validityPeriod")
  private Interval<Instant> validityPeriod;

  /** Number of refills authorized. */
  @JsonProperty("numberOfRepeatsAllowed")
  private Integer numberOfRepeatsAllowed;

  /** Amount of medication to supply per dispense. */
  @JsonProperty("dispenseQuantity")
  private NumericQuantity dispenseQuantity;

  /** Days supply per dispense. */
  @JsonProperty("expectedSupplyDuration")
  private NumericQuantity expectedSupplyDuration;
}
