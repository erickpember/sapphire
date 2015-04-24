// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a context Element of a DocumentReference model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DocumentReferenceContext")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DocumentReferenceContext {
  /** Main clinical acts documented. */
  @JsonProperty("events")
  private List<CodeableConcept> events;

  /** Time of service that is being documented. */
  @JsonProperty("period")
  private Interval<Instant> period;

  /** Kind of facility where patient was seen. */
  @JsonProperty("facilityType")
  private CodeableConcept facilityType;

  /** Additional details about where the content was created. */
  @JsonProperty("practiceSetting")
  private CodeableConcept practiceSetting;

  /** Source patient info. */
  @JsonProperty("sourcePatientInfoId")
  private Id<Patient> sourcePatientInfoId;

  /** Related thing, could be of any type. */
  @JsonProperty("related")
  private List<URI> related;
}
