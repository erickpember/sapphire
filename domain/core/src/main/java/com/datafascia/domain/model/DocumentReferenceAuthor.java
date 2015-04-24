// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Who|what is the author of a DocumentReference, only one instance of one
 * of these types:
 * Practitioner | Organization | Device | Patient | RelatedPerson
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DocumentReferenceAuthor")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DocumentReferenceAuthor {
  /** Who authored the document. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** Who authored the document. */
  @JsonProperty("organizationId")
  private Id<Organization> organizationId;

  /** Who authored the document. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;

  /** Who authored the document. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Who authored the document. */
  @JsonProperty("relatedPersonId")
  private Id<RelatedPerson> relatedPersonId;
}
