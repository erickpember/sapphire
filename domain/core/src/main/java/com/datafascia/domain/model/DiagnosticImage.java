// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.awt.Image;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a DiagnosticImage model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DiagnosticImage")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DiagnosticImage {
  /** Comment about the image (e.g. explanation). */
  @JsonProperty("comment")
  private String comment;

  /** Reference to the image source. */
  @JsonProperty("link")
  private Link<Image> link;
}
