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
 * Represents a MedicationDispense model, detailing the act of dispensing a Medication.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "MedicationDispense")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_MEDICATION_DISPENSE_ID)
public class MedicationDispense {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<MedicationDispense> id;

  /** In-progress | on-hold | completed | entered-in-error | stopped. */
  @JsonProperty("status")
  private MedicationDispenseStatus status;

  /** Who the dispense is for. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Practitioner responsible for dispensing medication. */
  @JsonProperty("dispenserId")
  private Id<Practitioner> dispenserId;

  /** Medication order that authorizes the dispense. */
  @JsonProperty("authorizingPrescriptionIds")
  private List<Id<MedicationPrescription>> authorizingPrescriptionIds;

  /** Trial fill, partial fill, emergency fill, etc. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Amount dispensed. */
  @JsonProperty("quantity")
  private NumericQuantity quantity;

  /** Days supply. */
  @JsonProperty("daysSupply")
  private NumericQuantity daysSupply;

  /** What medication was supplied. */
  @JsonProperty("medicationId")
  private Id<Medication> medicationId;

  /** Dispense processing time. */
  @JsonProperty("whenPrepared") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant whenPrepared;

  /** Hand-over time. */
  @JsonProperty("whenHandedOver") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant whenHandedOver;

  /** Where the prescription was sent. */
  @JsonProperty("destinationId")
  private Id<Location> destinationId;

  /** Who collected the medication, of type Patient or Practitioner. */
  @JsonProperty("receivers")
  private List<MedicationDispenseReceiver> receivers;

  /** Information about the dispense. */
  @JsonProperty("note")
  private String note;

  /** Medication administration instructions to the patient/carer. */
  @JsonProperty("dosageInstructions")
  private MedicationDispenseDosageInstruction dosageInstructions;

  /** Type of substitution. */
  @JsonProperty("substitutionType")
  private CodeableConcept substitutionType;

  /** Why the substitution was made. */
  @JsonProperty("substitutionReasons")
  private List<CodeableConcept> substitutionReasons;

  /** Who is responsible for the substitution. */
  @JsonProperty("substitutionResponsiblePartyIds")
  private List<Id<Practitioner>> substitutionResponsiblePartyIds;
}
