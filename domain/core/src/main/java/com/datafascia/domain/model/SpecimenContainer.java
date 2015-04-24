// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an element in the Specimen model, which represents a direct container of
 * a Specimen, such as a tube or slide.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "SpecimenContainer")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_SPECIMEN_CONTAINER_ID)
public class SpecimenContainer {
  /** External identifier for the container. */
  @JsonProperty("@id")
  private Id<SpecimenContainer> id;

  /** Textual description of the container. */
  @JsonProperty("description")
  private String description;

  /** Kind of container directly associated with specimen. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Container volume or size. */
  @JsonProperty("capacity")
  private NumericQuantity capacity;

  /** Quantity of specimen within container. */
  @JsonProperty("specimenQuantity")
  private NumericQuantity specimenQuantity;

  /** Additive associated with container. */
  @JsonProperty("additiveCodeableConcept")
  private CodeableConcept additiveCodeableConcept;

  /** Additive associated with container. */
  @JsonProperty("additiveReferenceId")
  private Id<Substance> additiveReferenceId;
}
