// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.awt.Image;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Anatomical location of a specimen or body part.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "BodySite")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_BODY_SITE_ID)
public class BodySite {
  /** Bodysite identifier. */
  @JsonProperty("@id")
  private Id<BodySite> id;

  /** Patient. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Named anatomical location. */
  @JsonProperty("code")
  private CodeableConcept code;

  /** Modification to location code. */
  @JsonProperty("modifier")
  private CodeableConcept modifier;

  /** Attached images. */
  @JsonProperty("images")
  private List<Link<Image>> images;
}
