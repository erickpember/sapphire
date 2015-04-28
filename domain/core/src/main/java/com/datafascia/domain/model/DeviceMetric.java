// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.DurationDeserializer;
import com.datafascia.common.jackson.DurationSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Duration;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Model which represents a measurement, calculation or setting capability of a medical device.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "DeviceMetric") @IdNamespace(URNFactory.NS_DEVICE_METRIC_ID)
public class DeviceMetric {
  /** Identifier by which this DeviceMetric is known. */
  @JsonProperty("@id")
  private Id<DeviceMetric> id;

  /** Type of metric. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Unit of metric. */
  @JsonProperty("unit")
  private CodeableConcept unit;

  /** Describes the link to the source Device. */
  @JsonProperty("sourceId")
  private Id<Device> sourceId;

  /** Describes the link to the parent DeviceComponent. */
  @JsonProperty("parentId")
  private Id<DeviceComponent> parentId;

  /** On | off | standby. */
  @JsonProperty("operationalStatus")
  private DeviceMetricOperationalStatus operationalStatus;

  /** Black | red | green | yellow | blue | magenta | cyan | white. */
  @JsonProperty("color")
  private DeviceMetricColor color;

  /** Measurement | setting | calculation | unspecified. */
  @JsonProperty("category")
  private DeviceMetricCategory category;

  /** Describes the measurement repetition time. */
  @JsonProperty("measurementPeriod") @JsonSerialize(using = DurationSerializer.class)
  @JsonDeserialize(using = DurationDeserializer.class)
  private Duration measurementPeriod;

  /** Describes the calibrations that have been performed or that are required to be performed. */
  @JsonProperty("calibrations")
  private List<DeviceMetricCalibration> calibrations;
}
