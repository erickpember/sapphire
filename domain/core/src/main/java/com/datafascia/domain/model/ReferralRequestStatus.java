// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Represents status of a ReferralRequest.
 */
public enum ReferralRequestStatus {
  DRAFT,
  REQUESTED,
  ACTIVE,
  CANCELED,
  ACCEPTED,
  REJECTED,
  COMPLETED
}