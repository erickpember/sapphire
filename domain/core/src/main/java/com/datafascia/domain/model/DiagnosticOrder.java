// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents Model DiagnosticOrder, which represents a record of a request for a diagnostic
 * investigation service to be performed.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DiagnosticOrder")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_DIAGNOSTIC_ORDER_ID)
public class DiagnosticOrder {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<DiagnosticOrder> id;

  /** What and/or who the test is about. */
  @JsonProperty("subject")
  private DiagnosticOrderSubject subject;

  /** Who ordered the test. */
  @JsonProperty("ordererId")
  private Id<Practitioner> ordererId;

  /** The associated encounter. */
  @JsonProperty("encounterId")
  private Id<Encounter> encounterId;

  /** Explanation / justification for test. */
  @JsonProperty("clinicalNotes")
  private String clinicalNotes;

  /** Additional clinical information, of type Observation | Condition | DocumentReference. */
  @JsonProperty("supportingInformation")
  private DiagnosticOrderSupportingInformation supportingInformation;

  /** If the whole order relates to specific specimens. */
  @JsonProperty("specimenIds")
  private List<Id<Specimen>> specimenIds;

  /**
   * proposed | draft | planned | requested | received | accepted | in-progress | review |
   * completed | cancelled | suspended | rejected | failed.
   */
  @JsonProperty("status")
  private DiagnosticOrderStatus status;

  /** Routine | urgent | stat | asap. */
  @JsonProperty("priority")
  private DiagnosticOrderPriority priority;

  /** A list of events of interest in the lifecycle. */
  @JsonProperty("events")
  private List<DiagnosticOrderEvent> events;

  /** The items the orderer requested. */
  @JsonProperty("items")
  private List<DiagnosticOrderItem> items;
}
