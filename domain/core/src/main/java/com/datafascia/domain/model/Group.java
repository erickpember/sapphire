// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.math.BigDecimal;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a Group model, a defined collection of entities that may be handled collectively
 * but which are not formally or legally recognized, as is an Organization.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Group")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_GROUP_ID)
public class Group {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Group> id;

  /** Person | animal | practitioner | device | medication | substance. */
  @JsonProperty("type")
  private GroupType type;

  /** Descriptive or actual. */
  @JsonProperty("actual")
  private Boolean actual;

  /** Kind of group members. */
  @JsonProperty("code")
  private CodeableConcept code;

  /** Label for group. */
  @JsonProperty("name")
  private String name;

  /** Number of members. */
  @JsonProperty("quantity")
  private BigDecimal quantity;

  /** Trait of group members. */
  @JsonProperty("characteristics")
  private List<GroupCharacteristic> characteristics;

  /** Who is in the group, of type Patient, Practitioner, Device, Medication or Substance. */
  @JsonProperty("members")
  private List<GroupMember> members;
}
