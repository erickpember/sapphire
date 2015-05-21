// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.reflect.AvroSchema;

/**
 * Role / Organization with which the Practitioner is associated.
 */
@Data @NoArgsConstructor
@JsonTypeName(URNFactory.MODEL_PREFIX + "PractitionerRole")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class PractitionerRole {
  /** The Organization where the Practitioner performs the roles associated. */
  @JsonProperty("managingOrganizationId")
  private Id<Organization> managingOrganizationId;

  /** A role which this Practitioner may perform. */
  @JsonProperty("role")
  private CodeableConcept role;

  /** A specific specialty of the Practitioner. */
  @JsonProperty("specialty")
  private CodeableConcept specialty;

  /** The period during which the practitioner is authorized to perform in this role. */
  @AvroSchema(Interval.INSTANT_INTERVAL_SCHEMA) @JsonProperty("period")
  private Interval<Instant> period;

  /** The location at which the practitioner provides care. */
  @JsonProperty("locationId")
  private Id<Location> locationId;

  /** The list of services that this worker provides for this role's Organization / Locations */
  @JsonProperty("healthcareServiceIds")
  private List<Id<HealthcareService>> healthcareServiceIds;
}
