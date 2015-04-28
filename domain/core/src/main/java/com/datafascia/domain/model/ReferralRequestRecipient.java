// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Receiver of referral / transfer of care request. Either of type Practitioner or Organization.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "ReferralRequestRecipient")
public class ReferralRequestRecipient {
  /** Receiver of referral / transfer of care request. */
  @JsonProperty("practitionerRecipientId")
  private Id<Practitioner> practitionerRecipientId;

  /** Receiver of referral / transfer of care request. */
  @JsonProperty("organizationRecipientId")
  private Id<Organization> organizationRecipientId;
}
