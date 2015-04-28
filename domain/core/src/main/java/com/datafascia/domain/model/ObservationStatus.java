// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Codes providing the status of an observation
 */
public enum ObservationStatus {
  REGISTERED,
  PRELIMINARY,
  FINAL,
  AMENDED,
  CANCELLED,
  ENTERED_IN_ERROR,
  UNKNOWN
}
