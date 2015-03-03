// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.api;

import lombok.extern.slf4j.Slf4j;

/**
 * Defines the parameter names for Datafascia API as it is shared among multiple classes
 */
@Slf4j
public class ApiParams {
  /** Starting identifier */
  public static final String START = "start";

  /** Active flag for patient */
  public static final String ACTIVE = "active";

  /** Maximum count for a list */
  public static final String COUNT = "count";

  /** Name of package */
  public static final String PACKAGE = "package";

  /** Patient identifier */
  public static final String PATIENT_ID = "patientId";

  /** Start time */
  public static final String START_TIME = "startTime";

  /** End time */
  public static final String END_TIME = "endTime";
}
