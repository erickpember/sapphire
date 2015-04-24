// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Details and position information for a physical place where services are provided and resources
 * and participants may be stored, found, contained or accommodated.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Location") @IdNamespace(URNFactory.NS_LOCATION_ID)
public class Location {
  /** Unique code identifying the location to its users. */
  @JsonProperty("@id")
  private Id<Location> id;

  /** Name of the location as used by humans. */
  @JsonProperty("name")
  private String name;

  /** Description of the Location which helps in finding or referencing the place. */
  @JsonProperty("description")
  private String description;

  /** Mode of a Location, instance | kind. */
  @JsonProperty("mode")
  private LocationMode mode;

  /** Type of function performed at the location. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Contact details at the location. */
  @JsonProperty("telecoms")
  private List<ContactPoint> telecoms;

  /** Physical location. */
  @JsonProperty("address")
  private Address address;

  /** Physical form of the location. */
  @JsonProperty("physicalType")
  private CodeableConcept physicalType;

  /** The absolute geographic location (lat/long/alt). */
  @JsonProperty("position")
  private Position position;

  /** The organization that is responsible for the provisioning and upkeep of the location. */
  @JsonProperty("managingOrganizationId")
  private Id<Organization> managingOrganizationId;

  /** Another Location which this Location is physically part of. */
  @JsonProperty("partOfId")
  private Id<Location> partOfId;

  /** Status of location: active | suspended | inactive. */
  @JsonProperty("status")
  private LocationStatus status;
}
