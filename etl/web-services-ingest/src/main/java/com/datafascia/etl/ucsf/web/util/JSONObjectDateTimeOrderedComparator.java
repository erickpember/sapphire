// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web.util;

import com.datafascia.etl.ucsf.web.UcsfWebGetProcessor;
import java.time.Instant;
import java.util.Comparator;
import org.json.simple.JSONObject;

/**
 * A comparator class for medication order JSON blobs.
 */
public class JSONObjectDateTimeOrderedComparator implements Comparator<JSONObject> {
  /**
   * Compares two medication order JSON blob dates.
   *
   * @param o1 The first blob.
   * @param o2 The second blob.
   * @return The comparator result.
   */
  @Override
  public int compare(JSONObject o1, JSONObject o2) {
    String ucsfTime1 = o1.get("DateTimeOrdered").toString();
    Instant dateTimeOrdered1 = UcsfWebGetProcessor.epicDateToInstant(ucsfTime1);
    String ucsfTime2 = o2.get("DateTimeOrdered").toString();
    Instant dateTimeOrdered2 = UcsfWebGetProcessor.epicDateToInstant(ucsfTime2);

    return dateTimeOrdered1.compareTo(dateTimeOrdered2);
  }
}
