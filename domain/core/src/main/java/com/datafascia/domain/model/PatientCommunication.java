// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A language which may be used to communicate with the patient about his or her health.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "PatientCommunication")
public class PatientCommunication {
  /** The language which can be used to communicate with the patient about his or her health. */
  @JsonProperty("language")
  private CodeableConcept language;

  /** This is the preferred language. */
  @JsonProperty("preferred")
  private Boolean preferred;
}
