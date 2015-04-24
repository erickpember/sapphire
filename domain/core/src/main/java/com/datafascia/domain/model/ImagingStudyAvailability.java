// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Represents availability status of an ImagingStudy model and also the status of a Series
 * element in the same ImagingStudy model.
 */
public enum ImagingStudyAvailability {
  ONLINE,
  OFFLINE,
  NEARLINE,
  UNAVAILABLE
}
