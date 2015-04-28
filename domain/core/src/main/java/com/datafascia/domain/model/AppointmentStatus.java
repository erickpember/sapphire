// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

/**
 * Represents status of an Appointment.
 */
public enum AppointmentStatus {
  PENDING,
  BOOKED,
  ARRIVED,
  FULFILLED,
  CANCELLED,
  NOSHOW
}
