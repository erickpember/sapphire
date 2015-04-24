// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Product that belongs to a Medication.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Product")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class Product {
  /** Powder | tablets | carton etc. */
  @JsonProperty("form")
  private CodeableConcept form;

  /** Active or inactive ingredients. */
  @JsonProperty("ingredients")
  private List<Ingredient> ingredients;

  /** Batches of this product. */
  @JsonProperty("batches")
  private List<ProductBatch> batches;
}
