// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.reflect.AvroSchema;

/**
 * Describes the event of a patient being given a dose of a non-vaccine medication.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "MedicationAdministration")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_MEDICATION_ADMINISTRATION_ID)
public class MedicationAdministration {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<MedicationAdministration> id;

  /** In progress | on hold | completed | entered in error | stopped. */
  @JsonProperty("status")
  private MedicationAdministrationStatus status;

  /** Who received the medication? */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Who administered substance? */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** Encounter administered as part of. */
  @JsonProperty("encounterId")
  private Id<Encounter> encounterId;

  /** Order administration performed against. */
  @JsonProperty("prescriptionId")
  private Id<MedicationPrescription> prescriptionId;

  /** True if medication was not administered. */
  @JsonProperty("prescription")
  private Boolean wasNotGiven;

  /** Reason(s) administration was not performed. */
  @JsonProperty("reasonsNotGiven")
  private List<CodeableConcept> reasonsNotGiven;

  /** Reason(s) administration was performed. */
  @JsonProperty("reasonsGiven")
  private List<CodeableConcept> reasonsGiven;

  /** Start and end time of administration. */
  @AvroSchema(Interval.INSTANT_INTERVAL_SCHEMA) @JsonProperty("effectiveTimePeriod")
  private Interval<Instant> effectiveTimePeriod;

  /** What was administered? */
  @JsonProperty("medicationId")
  private Id<Medication> medicationId;

  /** Device used to administer. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;

  /** Medication administration instructions to the patient/carer. */
  @JsonProperty("dosage")
  private MedicationAdministrationDosage dosage;
}
