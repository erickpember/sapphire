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
 * Represents a MedicationStatementInformationSource model.
 * This is a member of MedicationStatement that serves as a reference to an instance of
 * only one of the three following models: Patient, Practitioner, RelatedPerson
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX
    + "MedicationStatementInformationSource")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class MedicationStatementInformationSource {
  /** Source of information for a MedicationStatement. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Source of information for a MedicationStatement. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** Source of information for a MedicationStatement. */
  @JsonProperty("relatedPersonId")
  private Id<RelatedPerson> relatedPersonId;
}