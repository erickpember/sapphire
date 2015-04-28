// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Codes that provide an estimate of the degree to which quality issues have impacted on the value
 * of an observation.
 */
public enum ObservationReliability {
  OK,
  ONGOING,
  EARLY,
  QUESTIONABLE,
  CALIBRATING,
  ERROR,
  UNKNOWN
}
