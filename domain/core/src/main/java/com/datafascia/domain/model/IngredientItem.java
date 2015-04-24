// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents an item that makes up an Ingredient in a Medication,
 * either of type Substance or Medication.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "IngredientItem")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class IngredientItem {
  /** The Substance that makes up the containing Ingredient. */
  @JsonProperty("substanceId")
  private Id<Substance> substanceId;

  /** The Medication that makes up the containing Ingredient. */
  @JsonProperty("medicationId")
  private Id<Medication> medicationId;
}
