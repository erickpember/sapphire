// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.neovisionaries.i18n.LanguageCode;
import java.net.URI;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Represents a patient record.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode(callSuper = true)
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("Patient")
public class Patient extends Person {
  @JsonProperty("@id")
  private URI id;
  @JsonProperty("contacts")
  private List<Contact> contactDetails;
  @JsonProperty("creationDate")
  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
  private Date creationDate;
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
