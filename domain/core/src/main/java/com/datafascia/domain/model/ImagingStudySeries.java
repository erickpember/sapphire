// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Series Element, part of the ImagingStudy Model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ImagingStudySeries")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ImagingStudySeries {
  /** Numeric identifier of this series. */
  @JsonProperty("number")
  private BigDecimal number;

  /** The modality of the instances in the series. */
  @JsonProperty("modality")
  private String modality;

  /** Formal identifier for this series. */
  @JsonProperty("uid")
  private Id<Oid> uid;

  /** A description of the series. */
  @JsonProperty("description")
  private String description;

  /** Number of series related instances. */
  @JsonProperty("numberOfInstances")
  private BigDecimal numberOfInstances;

  /** ONLINE | OFFLINE | NEARLINE | UNAVAILABLE. */
  @JsonProperty("availability")
  private ImagingStudyAvailability availability;

  /** Retrieve URI. */
  @JsonProperty("url")
  private URI url;

  /** Body part examined. */
  @JsonProperty("bodySite")
  private Coding bodySite;

  /** Body part laterality. */
  @JsonProperty("laterality")
  private Coding laterality;

  /** When the series was started. */
  @JsonProperty("dateTime") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant dateTime;

  /** A single instance taken from a patient. */
  @JsonProperty("instances")
  private List<ImagingStudySeriesInstance> instances;
}
