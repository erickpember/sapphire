// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * A series of measurements taken by a device, with upper and lower limits. There may be more than
 * one dimension in the data.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("SampledData")
public class SampledData {
  /**
   * The base quantity that a measured value of zero represents. In addition, this provides the
   * units of the entire measurement series.
   */
  @JsonProperty("origin")
  private Quantity origin;

  /** The length of time between sampling times, measured in milliseconds. */
  @JsonProperty("period")
  private BigDecimal period;

  /**
   * A correction factor that is applied to the sampled data points before they are added to the
   * origin
   */
  @JsonProperty("factor")
  private BigDecimal factor;

  /**
   * The lower limit of detection of the measured points. This is needed if any of the data points
   * have the value &quot;L&quot; (lower than detection limit).
   */
  @JsonProperty("lowerLimit")
  private BigDecimal lowerLimit;

  /**
   * The upper limit of detection of the measured points. This is needed if any of the data points
   * have the value &quot;U&quot; (higher than detection limit).
   */
  @JsonProperty("upperLimit")
  private BigDecimal upperLimit;

  /**
   * The number of sample points at each time point. If this value is greater than one, then the
   * dimensions will be interlaced - all the sample points for a point in time will be recorded at
   * once.
   */
  @JsonProperty("dimensions")
  private long dimensions;

  /**
   * A series of data points.
   */
  @JsonProperty("data")
  private List<BigDecimal> data;
}
