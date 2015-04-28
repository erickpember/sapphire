// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
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
 * Production specification of a DeviceComponent.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "DeviceComponentSpecification") @IdNamespace(
    URNFactory.NS_DEVICE_COMPONENT_SPECIFICATION_ID)
public class DeviceComponentSpecification {
  /** Identifier by which this DeviceComponentSpecification is known. */
  @JsonProperty("@id")
  private Id<DeviceComponentSpecification> id;

  /** Specification type. */
  @JsonProperty("specType")
  private CodeableConcept specType;

  /** A printable string defining the component. */
  @JsonProperty("productionSpec")
  private String productionSpec;
}
