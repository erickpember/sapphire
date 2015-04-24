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
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A formally or informally recognized grouping of people or organizations.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Organization")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_ORGANIZATION_ID)
public class Organization {
  /** Identifies the organization across multiple systems. */
  @JsonProperty("@id")
  private Id<Organization> id;

  /** Name used for the organization. */
  @JsonProperty("name")
  private String name;

  /** Kind of organization. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Contact(s) for the organization for a certain purpose. */
  @JsonProperty("telecoms")
  private List<ContactPoint> telecoms;

  /** An address for the organization. */
  @JsonProperty("addresses")
  private List<Address> addresses;

  /** The organization of which this organization forms a part. */
  @JsonProperty("partOfId")
  private Id<Organization> partOfId;

  /** Location(s) the organization uses to provide services. */
  @JsonProperty("locationIds")
  private List<Id<Location>> locationIds;

  /** Whether the organization's record is still in active use. */
  @JsonProperty("active")
  private Boolean active;
}
