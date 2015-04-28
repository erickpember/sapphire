// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Represents the status of an encounter
 */
public enum EncounterStatus {
  PLANNED,
  ARRIVED,
  IN_PROGRESS,
  ON_LEAVE,
  FINISHED,
  CANCELLED
}
