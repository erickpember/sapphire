// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a MedicationStatement model.
 *
 * A record of medication taken by a patient, or medication that has been given to a patient where
 * the record is the result of a report from the patient or other clinician.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "MedicationStatement")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_MEDICATION_STATEMENT_ID)
public class MedicationStatement {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<MedicationStatement> id;

  /** Who was/is taking medication. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Source of information for this statement. */
  @JsonProperty("informationSource")
  private MedicationStatementInformationSource informationSource;

  /** When was the statement asserted? */
  @JsonProperty("dateAsserted") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant dateAsserted;

  /** In-progress | completed | entered-in-error.. */
  @JsonProperty("status")
  private MedicationStatementStatus status;

  /** True if medication was not given. */
  @JsonProperty("wasNotGiven")
  private Boolean wasNotGiven;

  /** If wasNotGiven is true, reasons why. */
  @JsonProperty("reasonsNotGiven")
  private List<CodeableConcept> reasonsNotGiven;

  /** If wasNotGiven is false, reasons why. */
  @JsonProperty("reasonForUseCodeableConcept")
  private CodeableConcept reasonForUseCodeableConcept;

  /** Condition reference as a reason for use. */
  @JsonProperty("reasonForUseReferenceId")
  private Id<Condition> reasonForUseReferenceId;

  /** Over what period was the medication consumed? */
  @JsonProperty("effectiveDateTime") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant effectiveDateTime;

  /** Over what period was the medication consumed? */
  @JsonProperty("effectivePeriod")
  private Interval<Instant> effectivePeriod;

  /** Further information about the statement. */
  @JsonProperty("note")
  private String note;

  /** What medication was taken? */
  @JsonProperty("medicationId")
  private Id<Medication> medicationId;

  /** Details on how medication was taken. */
  @JsonProperty("dosages")
  private List<MedicationStatementDosage> dosages;
}
