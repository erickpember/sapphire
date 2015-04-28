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
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * A practitioner that may be facilitating an EpisodeOfCare.
 * This represents the careTeam Element in the EpisodeOfCare Model,
 * which represents either a member that is either an Organization
 * or a Practitioner.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "EpisodeOfCareTeamMember")
public class EpisodeOfCareTeamMember {
  /** The team member, if a practitioner and not an organization. */
  @JsonProperty("practitionerMemberId")
  private Id<Practitioner> practitionerMemberId;

  /** The team member, if an organization and not a practitioner. */
  @JsonProperty("organizationMemberId")
  private Id<Organization> organizationMemberId;

  /** The role(s) that this team member is taking within this episode of care. */
  @JsonProperty("roles")
  private List<CodeableConcept> roles;

  /** The period of time this practitioner is performing some role within the episode of care. */
  @JsonProperty("period")
  private Interval<Instant> period;
}
