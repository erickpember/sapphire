// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Contact for an Organization for a certain purpose.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "OrganizationContact")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class OrganizationContact {
  /** The type of contact. */
  @JsonProperty("purpose")
  private String purpose;

  /** A name associated with the contact. */
  @JsonProperty("name")
  private Name name;

  /** Contact details (telephone, email, etc) for the contact. */
  @JsonProperty("telecoms")
  private List<ContactPoint> telecoms;

  /** Visiting or postal addresses for the contact. */
  @JsonProperty("address")
  private Address address;
}
