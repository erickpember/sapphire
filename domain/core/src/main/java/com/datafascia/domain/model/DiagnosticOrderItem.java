// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an Item Element as in a DiagnosticOrder model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DiagnosticOrderItem")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DiagnosticOrderItem {
  /** Code to indicate the item (test or panel) being ordered. */
  @JsonProperty("code")
  private CodeableConcept code;

  /** If this item relates to specific specimens. */
  @JsonProperty("specimens")
  private List<Id<Specimen>> specimens;

  /** Location of requested test (if applicable). */
  @JsonProperty("bodySiteCodeableConcept")
  private CodeableConcept bodySiteCodeableConcept;

  /** Location of requested test (if applicable). */
  @JsonProperty("bodySiteReferenceId")
  private Id<BodySite> bodySiteReferenceId;

  /**
   * Proposed | draft | planned | requested | received | accepted | in-progress | review |
   * completed | cancelled | suspended | rejected | failed.
   */
  @JsonProperty("status")
  private DiagnosticOrderItemStatus status;

  /** Events specific to this item. */
  @JsonProperty("event")
  private DiagnosticOrderEvent event;
}
