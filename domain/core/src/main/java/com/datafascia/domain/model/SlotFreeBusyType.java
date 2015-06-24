// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Represents how busy a Slot is for an Appointment.
 */
public enum SlotFreeBusyType {
  BUSY,
  FREE,
  BUSY_UNAVAILABLE,
  BUSY_TENTATIVE
}