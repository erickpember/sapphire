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
  /** Specific UCUM measurement.*/
  @JsonProperty("quantity")
  private NumericQuantity quantity;

  /** Code identifying what is being observed.*/
  @JsonProperty("code")
  private CodeableConcept code;

  /** Data associated with the observation.*/
  @JsonProperty("attachment")
  private Attachment attachment;

  /** Ratio of values associated with the observation.*/
  @JsonProperty("ratio")
  private Ratio ratio;

  /** Range of time associated with the observation.*/
  @JsonProperty("period")
  private Period period;

  /** Data sampled from a device.*/
  @JsonProperty("sampleData")
  private SampledData sampledData;

  /** A raw string value.*/
  @JsonProperty("text")
  private String text;
}
