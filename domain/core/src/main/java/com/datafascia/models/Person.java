// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.jackson.LocalDateDeserializer;
import com.datafascia.common.jackson.LocalDateSerializer;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.awt.Image;
import java.time.LocalDate;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a human being.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Person")
public class Person {
  /** A name associated with the individual.*/
  @JsonProperty("name")
  private Name name;

  /** Addresses for the individual.*/
  @JsonProperty("address")
  private Address address;

  /** The gender that the patient is considered to have for administration purposes.*/
  @JsonProperty("gender")
  private Gender gender;

  /** The date of birth for the individual.*/
  @JsonProperty("birthDate") @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate birthDate;

  /** Image of the person.*/
  @JsonProperty("photo")
  private Link<Image> photo;
}
