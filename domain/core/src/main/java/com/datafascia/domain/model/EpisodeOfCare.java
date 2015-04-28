// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
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
 * A Patient Administration Resource that represents an association between a patient and an
 * organization / healthcare provider during which time Encounters may occur.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "EpisodeOfCare") @IdNamespace(
    URNFactory.NS_EPISODE_OF_CARE_ID)
public class EpisodeOfCare {
  /** Identifier by which this EpisodeOfCare is known. */
  @JsonProperty("@id")
  private Id<EpisodeOfCare> id;

  /** Planned | waitlist | active | onhold | finished | cancelled. */
  @JsonProperty("status")
  private EpisodeOfCareStatus status;

  /** The previous statuses of this episode. */
  @JsonProperty("statusHistory")
  private List<EpisodeOfCareStatus> statusHistory;

  /** Specific type of EpisodeOfCare. */
  @JsonProperty("types")
  private List<CodeableConcept> types;

  /** The patient to whom this EpisodeOfCare relates. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** The organization that has assumed the specific responsibilities for the specified duration. */
  @JsonProperty("managingOrganizationId")
  private Id<Organization> managingOrganizationId;

  /** The interval during which the managing organization assumes the defined responsibility. */
  @JsonProperty("period")
  private Interval<Instant> period;

  /** A list of conditions/problems/diagnoses for which this episode of care is intended. */
  @JsonProperty("conditionIds")
  private List<Id<Condition>> conditionIds;

  /** Referral request(s), within which this EpisodeOfCare manages activities. */
  @JsonProperty("referralRequestIds")
  private List<Id<ReferralRequest>> referralRequestIds;

  /** The practitioner that is the care manager or care co-ordinator for this. */
  @JsonProperty("careManagerId")
  private Id<Practitioner> careManagerId;

  /** The list of practitioners that may be facilitating this episode of care. */
  @JsonProperty("careTeam")
  private List<EpisodeOfCareTeamMember> careTeam;
}
