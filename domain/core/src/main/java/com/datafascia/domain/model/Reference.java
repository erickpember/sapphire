// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Link from a resource to another resource. References are provided as a URL which
 * may be either absolute or relative. In our case, references are used when
 * our internal Id class is not used, particularly when a FHIR model
 * calls for an Element that refers to a model of "Any" type.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Reference")
public class Reference {
  /** Relative, internal, or absolute URL reference. */
  @JsonProperty("reference")
  private String reference;

  /** Text alternative for this resource. */
  @JsonProperty("display")
  private String display;
}
