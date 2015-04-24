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
 * Part of a MedicationDispense model, the receiver of the medication, of type Patient or
 * Practitioner.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "MedicationDispenseReceiver")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class MedicationDispenseReceiver {
  /** Who collected the medication. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Who collected the medication. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;
}
