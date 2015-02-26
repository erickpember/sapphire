// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Holds a given value for an observation.
 */
@Data @NoArgsConstructor @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "ObservationValue")
public class ObservationValue {
  @JsonProperty("quantity")
  private NumericQuantity quantity;

  @JsonProperty("code")
  private CodeableConcept code;

  @JsonProperty("attachment")
  private Attachment attachment;

  @JsonProperty("ratio")
  private Ratio ratio;

  @JsonProperty("period")
  private Period period;

  @JsonProperty("sampleData")
  private SampledData sampledData;

  @JsonProperty("text")
  private String text;
}
