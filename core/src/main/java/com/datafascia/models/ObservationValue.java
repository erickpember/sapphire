// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Holds a given value for an observation.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("ObservationValue")
public class ObservationValue {
  @JsonProperty("quantity")
  private Quantity quantity;

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
