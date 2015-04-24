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
 * Represents a condition, problem or diagnosis.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Condition")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_CONDITION_ID)
public class Condition {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Condition> id;

  /** Who has the condition? */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Encounter when the condition was first asserted. */
  @JsonProperty("encounterId")
  private Id<Encounter> encounterId;

  /** Person who asserts this condition, Practitioner or Patient. */
  @JsonProperty("asserterId")
  private Id<Person> asserterId;

  /** Date that the condition was asserted. */
  @JsonProperty("dateAsserted") @JsonSerialize(using = LocalDateSerializer.class)
  @JsonDeserialize(using = LocalDateDeserializer.class)
  private LocalDate dateAsserted;

  /** Identification of the condition, problem or diagnosis. */
  @JsonProperty("code")
  private CodeableConcept code;

  /** E.g. complaint | system | finding | diagnosis. */
  @JsonProperty("category")
  private CodeableConcept category;

  /** E.g. provisional | working | confirmed | refuted | entered-in-error | unknown. */
  @JsonProperty("clinicalStatus")
  private ConditionClinicalStatus clinicalStatus;

  /** Subjective severity of the condition. */
  @JsonProperty("severity")
  private CodeableConcept severity;

  /** Date/Time prescription was authorized. */
  @JsonProperty("dateWritten") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant dateWritten;

  /** Degree of confidence. */
  @JsonProperty("certainty")
  private CodeableConcept certainty;

  /** Estimated or actual date of onset (LocalDate), or age (NumericQuantity). */
  @JsonProperty("onset")
  private ConditionOnset onset;

  /**
   * If / when in resolution/remission, date (LocalDate), boolean (CodeableConcept)
   * or age (NumericQuantity).
   */
  @JsonProperty("abatement")
  private ConditionAbatement abatement;

  /** Stage/grade, usually assessed formally. */
  @JsonProperty("stage")
  private ConditionStage stage;

  /** Supporting evidence. */
  @JsonProperty("evidence")
  private List<ConditionEvidence> evidence;

  /** Anatomical location, if relevant. */
  @JsonProperty("locations")
  private List<ConditionLocation> locations;

  /** Causes for this Condition. */
  @JsonProperty("dueTo")
  private List<ConditionDueTo> dueTo;

  /** Anatomical location, if relevant. */
  @JsonProperty("occurredFollowing")
  private List<ConditionOccurredFollowing> occurredFollowing;

  /** Additional information about the Condition. */
  @JsonProperty("notes")
  private String notes;
}
