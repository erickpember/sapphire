// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

/**
 * Tuple field names
 */
public abstract class F {

  public static final String BYTES = "bytes";
  public static final String INGEST_MESSAGE = "ingestMessage";
  public static final String INSTITUTION_ID = "instituionId";
  public static final String FACILITY_ID = "facilityId";
  public static final String MESSAGE = "message";
  public static final String EVENT = "event";

  // Private constructor disallows creating instances of this class.
  private F() {
  }
}
