// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a ImagingStudySeriesInstance Element,
 * part of the ImagingStudySeries Element,
 * part of the ImagingStudyModel.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ImagingStudySeriesInstance")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ImagingStudySeriesInstance {
  /** The number of this instance in the series. */
  @JsonProperty("number")
  private BigDecimal number;

  /** Formal identifier for this instance. */
  @JsonProperty("uid")
  private Id<Oid> uid;

  /** DICOM class type. */
  @JsonProperty("sopClassOid")
  private Id<Oid> sopClassOid;

  /** Type of instance (image, etc). */
  @JsonProperty("type")
  private String type;

  /** Description (0070,0080 | 0040,A043 > 0008,0104 | 0042,0010 | 0008,0008). */
  @JsonProperty("title")
  private String title;

  /** Content of the instance. */
  @JsonProperty("content")
  private List<Attachment> content;
}
