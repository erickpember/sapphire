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
 * A specific type of Service that may be delivered of performed for HealthCareService.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "HealthcareServiceType")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class HealthcareServiceType {
  /** The specific type of service being delivered or performed. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Collection of specialties handled by the Service Site. This is more of a medical Term. */
  @JsonProperty("specialties")
  private List<CodeableConcept> specialties;
}
