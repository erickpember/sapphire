// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.IdDeserializer;
import com.datafascia.common.jackson.IdSerializer;
import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.LanguageCode;
import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a patient record.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Patient") @IdNamespace(URNFactory.NS_PATIENT_ID)
@ToString(callSuper = true)
public class Patient extends Person {
  /** Identifier for the patient.*/
  @JsonProperty("@id")
  @JsonDeserialize(using = IdDeserializer.class) @JsonSerialize(using = IdSerializer.class)
  private Id<Patient> id;

  /** Identifier used by a given institution's internal database.*/
  @JsonProperty("institutionPatientId")
  private String institutionPatientId;

  /** Account number from HL7 field PID-18.*/
  private String accountNumber;

 /** A list of contacts associated with the patient.*/
  @JsonProperty("contacts")
  private List<Contact> contactDetails;

  /** Date and time the patient was entered into the system.*/
  @JsonProperty("creationDate") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant creationDate;

  /** Indicates if the individual is deceased or not.*/
  @JsonProperty("deceased")
  private boolean deceased;

  /** The patient's most recent marital (civil) status.*/
  @JsonProperty("maritalStatus")
  private MaritalStatus maritalStatus;

  /** The patient's race.*/
  @JsonProperty("race")
  private Race race;

  /** Languages which may be used to communicate with the patient about his or her health.*/
  @JsonProperty("languages")
  private List<LanguageCode> langs;

  /** Patient's nominated care provider.*/
  @JsonProperty("careProvider")
  private List<Caregiver> careProvider;

  /** Organization that is the custodian of the patient record.*/
  @JsonProperty("managingOrganization")
  private String managingOrg;

  /** last encounter for the patient */
  @JsonIgnore
  private Id<Encounter> lastEncounterId;

  /** Whether this patient record is in active use.*/
  @JsonProperty("active")
  private boolean active;
}
