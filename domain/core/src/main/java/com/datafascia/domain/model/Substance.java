// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
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
 * Represents a Substance that makes up Medication.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Substance")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_SUBSTANCE_ID)
public class Substance {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Substance> id;

  /** What kind of substance this is. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Textual description of the substance, comments. */
  @JsonProperty("description")
  private String description;

  /** When no longer valid to use. */
  @JsonProperty("expiry") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant expiry;

  /** Amount of substance in the package. */
  @JsonProperty("quantity")
  private NumericQuantity quantity;

  /** Composition information about the substance. */
  @JsonProperty("ingredients")
  private List<SubstanceIngredient> ingredients;
}
