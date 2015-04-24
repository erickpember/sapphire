// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.jackson.LocalDateDeserializer;
import com.datafascia.common.jackson.LocalDateSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an Immunization model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Immunization")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_IMMUNIZATION_ID)
public class Immunization {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Immunization> id;

  /** Vaccination administration date. */
  @JsonProperty("date") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant date;

  /** Vaccine product administered. */
  @JsonProperty("vaccineType")
  private CodeableConcept vaccineType;

  /** Who was immunized? */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /**
   * Was immunization given? If false, reasonNotGiven SHALL be absent. If true, there SHALL be no
   * reaction or reasonGiven present.
   */
  @JsonProperty("wasNotGiven")
  private Boolean wasNotGiven;

  /** Is this a self-reported record? */
  @JsonProperty("reported")
  private Boolean reported;

  /** Who administered vaccine? */
  @JsonProperty("performerId")
  private Id<Practitioner> performerId;

  /** Who ordered vaccination? */
  @JsonProperty("requesterId")
  private Id<Practitioner> requesterId;

  /** Encounter administered as part of. */
  @JsonProperty("encounterId")
  private Id<Encounter> encounterId;

  /** Vaccine manufacturer. */
  @JsonProperty("manufacturerId")
  private Id<Organization> manufacturerId;

  /** Where did the vaccination occur? */
  @JsonProperty("locationId")
  private Id<Location> locationId;

  /** Vaccine lot number. */
  @JsonProperty("lotNumber")
  private String lotNumber;

  /** Vaccine expiration date. */
  @JsonProperty("expirationDate") @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate expirationDate;

  /** Body site vaccine was administered. */
  @JsonProperty("site")
  private CodeableConcept site;

  /** How vaccine entered body. */
  @JsonProperty("route")
  private CodeableConcept route;

  /** Amount of vaccine administered. */
  @JsonProperty("doseQuantity")
  private NumericQuantity doseQuantity;

  /** Why immunization occurred, if it did. */
  @JsonProperty("reasonsGiven")
  private List<CodeableConcept> reasonsGiven;

  /** Why immunization did not occur, if it didn't. */
  @JsonProperty("reasonsNotGiven")
  private List<CodeableConcept> reasonsNotGiven;

  /** Details of a reaction that follows immunization. */
  @JsonProperty("reactions")
  private List<ImmunizationReaction> reactions;

  /** What protocol was followed. */
  @JsonProperty("vaccinationProtocols")
  private List<ImmunizationVaccinationProtocol> vaccinationProtocols;
}
