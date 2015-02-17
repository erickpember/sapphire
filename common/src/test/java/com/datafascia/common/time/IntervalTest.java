// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.time;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test for Interval.
 */
@Slf4j
public class IntervalTest {
  @Test
  public void beforeDuringAfter() throws ParseException {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
    Date before = sdf.parse("2015-01-01 00:00:00");
    Date start = sdf.parse("2015-01-02 00:00:00");
    Date middle = sdf.parse("2015-01-02 01:00:00");
    Date end = sdf.parse("2015-02-01 00:00:00");
    Date after = sdf.parse("2015-02-01 00:00:01");

    Interval<Date> interval = new Interval<Date>(start, end);
    // Start inclusive
    assertTrue(interval.contains(start));

    // In between value
    assertTrue(interval.contains(middle));

    // End exclusive
    assertFalse(interval.contains(end));

    // Exclude values before
    assertFalse(interval.contains(before));

    // Exclude values after
    assertFalse(interval.contains(after));
  }
}
