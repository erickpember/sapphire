// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web;

/**
 * Enumeration of know statuses for medication administrations.
 */
public enum UcsfAdminStatusEnum {
  DUE,
  GIVEN,
  CANCELEDENTRY,
  NEWBAG,
  RATEVERIFY,
  STOPPED;
}
