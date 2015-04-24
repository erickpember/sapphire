// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * The absolute geographic location.
 */
@Data @NoArgsConstructor
@JsonTypeName(URNFactory.MODEL_PREFIX + "Position")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class Position {
  /** Altitude with WGS84 datum. */
  @JsonProperty("altitude")
  private BigDecimal altitude;

  /** Latitude with WGS84 datum. */
  @JsonProperty("latitude")
  private BigDecimal latitude;

  /** Longitude with WGS84 datum. */
  @JsonProperty("longitude")
  private BigDecimal longitude;
}
