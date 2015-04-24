// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
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
 * Represents the MedicationPrescription model, a prescription of medication.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "MedicationPrescription")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_PRESCRIPTION_ID)
public class MedicationPrescription {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<MedicationPrescription> id;

  /** Date/Time prescription was authorized. */
  @JsonProperty("dateWritten") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant dateWritten;

  /** Active | on hold | completed | entered in error | stopped | superceded | draft. */
  @JsonProperty("status")
  private MedicationPrescriptionStatus status;

  /** Who the prescription is for. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Who ordered the medication(s). */
  @JsonProperty("prescriberId")
  private Id<Practitioner> prescriberId;

  /** Created during encounter / admission / stay. */
  @JsonProperty("encounterId")
  private Id<Encounter> encounterId;

  /** Reason or indication for writing the prescription. CodableConcept or Condition. */
  @JsonProperty("reason")
  private MedicationPrescriptionReason reason;

  /** Medication to be taken. */
  @JsonProperty("medicationId")
  private Id<Medication> medicationId;

  /** How medication should be taken. */
  @JsonProperty("dosageInstructions")
  private List<MedicationPrescriptionDosageInstruction> dosageInstructions;

  /** Medication supply authorization. */
  @JsonProperty("dispense")
  private MedicationPrescriptionDispense dispense;

  /** Any restrictions on medication substitution? */
  @JsonProperty("substitution")
  private MedicationPrescriptionSubstitution substitution;
}
