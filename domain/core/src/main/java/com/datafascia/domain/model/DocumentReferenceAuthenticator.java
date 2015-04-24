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
 * Who|what is the authenticator of a DocumentReference, only one instance of one
 * of these types:
 * Practitioner | Organization
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DocumentReferenceAuthenticator")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class DocumentReferenceAuthenticator {
  /** Who authenticated the document. */
  @JsonProperty("practitionerId")
  private Id<Practitioner> practitionerId;

  /** Who authenticated the document. */
  @JsonProperty("organizationId")
  private Id<Organization> organizationId;
}
