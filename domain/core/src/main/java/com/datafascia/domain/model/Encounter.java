// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.DurationDeserializer;
import com.datafascia.common.jackson.DurationSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.reflect.AvroSchema;

/**
 * Represents a patient visit.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Encounter") @IdNamespace(URNFactory.NS_ENCOUNTER_ID)
public class Encounter {
  /** Identity of the resource assigned by the server responsible for storing it. */
  @JsonProperty("@id")
  private Id<Encounter> id;

  /** Institution encounter identifier, for example, value from HL7 field PV1-19. */
  @JsonProperty("identifier")
  private String identifier;

  /** Status of the encounter. */
  @JsonProperty("status")
  private EncounterStatus status;

  /** Previous Statuses of the encounter */
  @JsonProperty("statusHistory")
  private List<EncounterStatusHistory> statusHistory;

  /** Class of the encounter. */
  @JsonProperty("eClass")
  private EncounterClass eClass;

  /**
   * Specific type of encounter (e.g. e-mail consultation, surgical day-care, skilled nursing,
   * rehabilitation).
   */
  @JsonProperty("types")
  private List<EncounterType> types;

  /** The patient present at the encounter. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** An episode of care that this encounter should be recorded against. */
  @JsonProperty("episodeOfCare")
  private Id<EpisodeOfCare> episodeOfCareId;

  /** Incoming referral requests. */
  @JsonProperty("incomingReferralRequestIds")
  private List<Id<EpisodeOfCare>> incomingReferralRequestIds;

  /** List of participants involved in the encounter. */
  @JsonProperty("participants")
  private List<Participant> participants;

  /** The appointment that scheduled this encounter. */
  @JsonProperty("fulfillsId")
  private Id<Appointment> fulfillsId;

  /** The start and end time of the encounter. */
  @AvroSchema(Interval.INSTANT_INTERVAL_SCHEMA) @JsonProperty("period")
  private Interval<Instant> period;

  /** Quality of time the encounter lasted (less time absent). */
  @JsonProperty("length") @JsonSerialize(using = DurationSerializer.class)
  @JsonDeserialize(using = DurationDeserializer.class)
  private Duration length;

  /** The reasons for the visit. */
  @JsonProperty("reasons")
  private List<CodeableConcept> reasons;

  /** Reason(s) the encounter takes place. Can be a link to "Any" type of Resource. */
  @JsonProperty("indications")
  private List<Reference> indications;

  /** Indicates the urgency of the encounter. */
  @JsonProperty("priority")
  private EncounterPriority priority;

  /** Details about an admission to a clinic. */
  @JsonProperty("hospitalization")
  private Hospitalization hospitalization;

  /** List of locations at which the patient has been. */
  @JsonProperty("locations")
  private List<EncounterLocation> locations;

  /** The custodian organization of this Encounter record. */
  @JsonProperty("serviceProviderId")
  private Id<Organization> serviceProviderId;

  /** Another Encounter of which this encounter is a part. */
  @JsonProperty("partOfId")
  private Id<Encounter> partOfId;

  /** Observations part of this Encounter. Conforms to our storage structure, not part of FHIR. */
  @JsonProperty("observations")
  private List<Observation> observations;
}
