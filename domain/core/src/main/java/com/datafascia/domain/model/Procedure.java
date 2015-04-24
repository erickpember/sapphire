// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Procedure model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Procedure")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_PROCEDURE_ID)
public class Procedure {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Procedure> id;

  /** Who the procedure was performed on. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** In-progress | aborted | completed | entered-in-error. */
  @JsonProperty("status")
  private ProcedureStatus status;

  /** Classification of the procedure. */
  @JsonProperty("category")
  private CodeableConcept category;

  /** Identification of the procedure. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Precise location details. */
  @JsonProperty("bodySites")
  private List<ProcedureBodySite> bodySites;

  /** Reason procedure performed. */
  @JsonProperty("indications")
  private List<CodeableConcept> indications;

  /** The people who performed the procedure. */
  @JsonProperty("performers")
  private List<ProcedurePerformer> performers;

  /** Date the procedure was performed. */
  @JsonProperty("performedDateTime") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant performedDateTime;

  /** Period the procedure was performed. */
  @JsonProperty("performedPeriod")
  private Interval<Instant> performedPeriod;

  /** The encounter when procedure was performed. */
  @JsonProperty("encounterId")
  private Id<Encounter> encounterId;

  /** Where the procedure happened. */
  @JsonProperty("locationId")
  private Id<Location> locationId;

  /** What was the result of procedure? */
  @JsonProperty("outcome")
  private CodeableConcept outcome;

  /** Any report that results from the procedure. */
  @JsonProperty("reportIds")
  private List<Id<DiagnosticReport>> reportIds;

  /** Complication following the procedure. */
  @JsonProperty("complications")
  private List<CodeableConcept> complications;

  /** Instructions for follow up. */
  @JsonProperty("followups")
  private List<CodeableConcept> followups;

  /** A procedure that is related to this one. */
  @JsonProperty("relatedItems")
  private List<ProcedureRelatedItem> relatedItems;

  /** Additional information about procedure. */
  @JsonProperty("notes")
  private String notes;

  /** Device changed in procedure. */
  @JsonProperty("devices")
  private List<ProcedureDevice> devices;

  /** Items used during procedure, either Device, Medication or Substance. */
  @JsonProperty("used")
  private ProcedureUsedItem used;
}
