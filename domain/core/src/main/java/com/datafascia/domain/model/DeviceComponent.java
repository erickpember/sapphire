// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a FHIR Model which describes the characteristics, operational status and capabilities
 * of a medical-related component of a medical device.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "DeviceComponent")
@IdNamespace(URNFactory.NS_DEVICE_COMPONENT_ID)
public class DeviceComponent {
  /** Identifier by which this DeviceComponent is known. */
  @JsonProperty("@id")
  private Id<DeviceComponent> id;

  /** What kind of component this is. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Recent system change timestamp. */
  @JsonProperty("lastSystemChange") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant lastSystemChange;

  /** A source device for this component. */
  @JsonProperty("sourceId")
  private Id<Device> sourceId;

  /** Parent resource link. */
  @JsonProperty("parentId")
  private Id<DeviceComponent> parentId;

  /** Component operational status. */
  @JsonProperty("operationalStatuses")
  private List<CodeableConcept> operationalStatuses;

  /** Current supported parameter group. */
  @JsonProperty("parameterGroup")
  private CodeableConcept parameterGroup;

  /** Other | chemical | electrical | impedance | nuclear | optical | thermal | biological, etc. */
  @JsonProperty("measurementPrinciple")
  private DeviceComponentMeasurementPrinciple measurementPrinciple;

  /** Production specification of the component. */
  @JsonProperty("productionSpecifications")
  private List<DeviceComponentSpecification> productionSpecifications;

  /** Language code for the human-readable text strings produced by this device. */
  @JsonProperty("languageCode")
  private CodeableConcept languageCode;
}
