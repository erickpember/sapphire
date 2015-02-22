// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.persist.Id;
import com.datafascia.jackson.IdDeserializer;
import com.datafascia.jackson.IdSerializer;
import com.datafascia.jackson.InstantDeserializer;
import com.datafascia.jackson.InstantSerializer;
import com.datafascia.urn.URNFactory;
import com.datafascia.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.LanguageCode;
import java.net.URI;
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
  @JsonProperty("@id")
  @JsonDeserialize(using = IdDeserializer.class) @JsonSerialize(using = IdSerializer.class)
  private Id<Patient> id;
  @JsonProperty("contacts")
  private List<Contact> contactDetails;
  @JsonProperty("creationDate") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant creationDate;
  @JsonProperty("deceased")
  private boolean deceased;
  @JsonProperty("maritalStatus")
  private MaritalStatus maritalStatus;
  @JsonProperty("race")
  private Race race;
  @JsonProperty("languages")
  private List<LanguageCode> langs;
  @JsonProperty("careProvider")
  private List<Caregiver> careProvider;
  @JsonProperty("managingOrganization")
  private String managingOrg;
  @JsonProperty("active")
  private boolean active;
  @JsonProperty("institutionPatientId")
  private URI institutionPatientId;
}
