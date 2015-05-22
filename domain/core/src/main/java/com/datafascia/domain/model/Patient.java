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
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Represents a patient record.
 */
@AllArgsConstructor @Builder @Data @EqualsAndHashCode(callSuper = true) @NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Patient") @IdNamespace(URNFactory.NS_PATIENT_ID)
@ToString(callSuper = true)
public class Patient extends Person {
  /** Identifier for the patient.*/
  @JsonProperty("@id")
  private Id<Patient> id;

  /** Identifier used by a given institution's internal database.*/
  @JsonProperty("institutionPatientId")
  private String institutionPatientId;

  /** Indicates if the individual is deceased or not.*/
  @JsonProperty("deceased")
  private boolean deceased;

  /** The patient's most recent marital (civil) status.*/
  @JsonProperty("maritalStatus")
  private MaritalStatus maritalStatus;

  /** Whether patient is part of a multiple birth.*/
  @JsonProperty("multipleBirthBoolean")
  private boolean multipleBirthBoolean;

  /** If patient is part of a multiple birth, how many were born?*/
  @JsonProperty("multipleBirthInteger")
  private BigDecimal multipleBirthInteger;

  /** One or more contact party (guardian/partner/friend/etc.) for the patient.*/
  @JsonProperty("contacts")
  private List<PatientContact> contacts;

  /** If this patient is a non-human animal, animal-specific details about the patient.*/
  @JsonProperty("animal")
  private PatientAnimal animal;

  /** A language the Patient speaks. FHIR has this as a list. */
  @JsonProperty("communication")
  private PatientCommunication communication;

  /** Patient's nominated care providers.*/
  @JsonProperty("careProviders")
  private List<PatientCareProvider> careProviders;

  /** Link to another Patient. Element "link" in FHIR.*/
  @JsonProperty("patientLinks")
  private List<PatientLink> patientLinks;

  /** Last encounter for the patient. Not in the FHIR Patient Model.*/
  @JsonProperty("lastEncounterId")
  private Id<Encounter> lastEncounterId;

  /** The patient's race. Not in the FHIR Patient Model.*/
  @JsonProperty("race")
  private Race race;

  /** Account number from HL7 field PID-18. Not in the FHIR Patient Model.*/
  private String accountNumber;

  /** Date and time the patient was entered into the system. Not in the FHIR Patient model.*/
  @JsonProperty("creationDate") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant creationDate;

  /** Whether this patient's record is in active use.*/
  @JsonProperty("active")
  private boolean active;
}
