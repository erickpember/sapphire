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
 * An Element in the Patient Model representing a contact party for the Patient.
 * This shall at least contain a contact's details or a reference to an organization.
 */
@Data @NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "PatientContact")
public class PatientContact {
  /** Relationships the Contact has to the Patient. */
  @JsonProperty("relationships")
  private List<CodeableConcept> relationships;

  /** A name associated with the contact person. */
  @JsonProperty("name")
  private HumanName name;

  /** Ways to reach the person. */
  @JsonProperty("telecoms")
  private List<ContactPoint> telecoms;

  /** Address for the contact person. */
  @JsonProperty("address")
  private Address address;

  /** Male | female | other | unknown. */
  @JsonProperty("gender")
  private Gender gender;

  /** Organization that is associated with the contact. */
  @JsonProperty("organizationId")
  private Id<Organization> organizationId;

  /** The period during which this contact is valid to be contacted relating to this Patient. */
  @AvroSchema(Interval.INSTANT_INTERVAL_SCHEMA) @JsonProperty("period")
  private Interval<Instant> period;
}
