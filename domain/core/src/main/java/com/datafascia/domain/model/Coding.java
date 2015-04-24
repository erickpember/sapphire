// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URI;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Coding datatype.
 * A coding is a representation of a defined concept using a symbol from a defined "code system".
 * While similar to the more common CodeableConcept, this is a separate Complex Type in the Fhir
 * ontology.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Coding")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class Coding {
  /** If a valueSet is provided, a system URI is required. Identity of the terminology system. */
  @JsonProperty("system")
  private URI system;

  /** Version of the system - if relevant. */
  @JsonProperty("version")
  private String version;

  /** Symbol in syntax defined by the system. */
  @JsonProperty("code")
  private String code;

  /** Representation defined by the system. */
  @JsonProperty("display")
  private String display;

  /** If this code was chosen directly by the user. */
  @JsonProperty("primary")
  private Boolean primary;
}
