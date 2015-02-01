// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.persist.Id;
import com.datafascia.jackson.IdDeserializer;
import com.datafascia.jackson.IdSerializer;
import com.datafascia.urn.annotations.IDNamespace;
import com.datafascia.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Setter;

/**
 * Represents a patient visit.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("Encounter") @IDNamespace(URNFactory.NS_ENCOUNTER_ID)
public class Encounter {
  /** Identifies the encounter. */
  @JsonProperty("@id")
  @JsonDeserialize(using = IdDeserializer.class) @JsonSerialize(using = IdSerializer.class)
  private Id<Encounter> id;

  /** Status of the encounter. */
  @JsonProperty("status")
  private EncounterStatus status;

  /** Class of the encounter. */
  @JsonProperty("class")
  private EncounterClass eclass;

  /**
   * Specific type of encounter (e.g. e-mail consultation, surgical day-care, skilled nursing,
   * rehabilitation).
   */
  @JsonProperty("type")
  private EncounterType type;

  /** The start and end time of the encounter. */
  @JsonProperty("period")
  private Period period;

  /** The reason for the visit. */
  @JsonProperty("reason")
  private CodeableConcept reason;

  /** This is a reference to another resource idenified by the 'id' elements we're using. */
  @JsonProperty("indication")
  private URI indication;

  /** Indicates the urgency of the encounter. */
  @JsonProperty("priority")
  private EncounterPriority priority;

  /** Department or team providing care. */
  @JsonProperty("serviceProvider")
  private URI serviceProvider;

  /** Details about an admission to a clinic. */
  @JsonProperty("hospitalisation")
  private Hospitalization hospitalisation;

  /** List of locations at which the patient has been. */
  @JsonProperty("location")
  private List<Location> location;

  /** The main practitioner responsible for providing the service. */
  @JsonProperty("participants")
  private List<Participant> participants;

  @JsonProperty("linkedTo")
  private URI linkedTo;

  /** The main practitioner responsible for providing the service. */
  @JsonProperty("observations")
  private List<Observation> observations;

  /** The patient present at the encounter. */
  @JsonProperty("patient")
  private URI patient;
}
