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
 * A Patient Care Model that is used to record details about a request for referral service or
 * transfer of a patient to the care of another provider or provider organization.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "ReferralRequest") @IdNamespace(
    URNFactory.NS_REFERRAL_REQUEST_ID)
public class ReferralRequest {
  /** Identifier by which this ReferralRequest is known. */
  @JsonProperty("@id")
  private Id<ReferralRequest> id;

  /** Draft | requested | active | canceled | accepted | rejected | completed. */
  @JsonProperty("status")
  private ReferralRequestStatus status;

  /** Referral/Transition of care request type. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** The clinical specialty (discipline) for which the referral is requested. */
  @JsonProperty("specialty")
  private CodeableConcept specialty;

  /** Urgency of referral / transfer of care request. */
  @JsonProperty("priority")
  private CodeableConcept priority;

  /** Patient referred to care or transfer. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Requester of referral. */
  @JsonProperty("requester")
  private ReferralRequestRequester requester;

  /** Receiver of request, of type Practitioner or Organization. */
  @JsonProperty("recipients")
  private List<ReferralRequestRecipient> recipients;

  /** Encounter. */
  @JsonProperty("encounterId")
  private Id<Encounter> encounterId;

  /** Date referral / transfer of care request is sent. */
  @JsonProperty("dateSent")
  private Instant dateSent;

  /** Reason for referral / transfer of care request. */
  @JsonProperty("reason")
  private CodeableConcept reason;

  /** A textual description of the referral. */
  @JsonProperty("description")
  private String description;

  /** Service(s) requested. */
  @JsonProperty("servicesRequested")
  private List<CodeableConcept> servicesRequested;

  /** Additional information to support referral or transfer of care request. Can be any type. */
  @JsonProperty("supportingInformation")
  private List<Reference> supportingInformation;

  /** Requested service(s) fulfillment time. */
  @JsonProperty("fulfillmentTime")
  private Interval<Instant> fulfillmentTime;
}
