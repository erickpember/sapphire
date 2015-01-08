// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Holds a given value for an observation.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
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
