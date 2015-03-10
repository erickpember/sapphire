// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.time;

import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

/**
 * Instant formatter constants
 */
public class InstantFormatter {

  /**
   * Always output millisecond resolution even if the fraction of the second is 0.
   */
  public static final DateTimeFormatter ISO_INSTANT_MILLI =
      DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX").withZone(ZoneOffset.UTC);
}
