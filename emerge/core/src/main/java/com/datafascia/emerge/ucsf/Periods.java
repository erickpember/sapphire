// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Calculates time periods, such as nursing shifts.
 */
public class Periods {

  private static final int YESTERDAY = -1;
  private static final int TODAY = 0;
  private static final int TOMORROW = 1;

  /**
   * Based on the current time, return the start and end of either the current or previous shift.
   * If the current time is within 4 hours of the start of the current shift, then return the
   * previous shift, otherwise return the current shift.
   *
   * @param clock
   *     clock with the relevant time zone.
   * @return
   *     The start and end time of the current or previous shift.
   */
  public static PeriodDt getCurrentOrPriorShift(Clock clock) {
    ZonedDateTime now = ZonedDateTime.now(clock);
    int nowHour = now.getHour();
    int shiftStartHour = -1;
    int shiftStartDay = TODAY;
    int shiftEndHour = -1;
    int shiftEndDay = TODAY;

    if (nowHour < 11) {
      shiftStartHour = 19;
      shiftStartDay = YESTERDAY;
      shiftEndHour = 7;
      shiftEndDay = TODAY;
    } else if (nowHour >= 11 && nowHour < 23) {
      shiftStartHour = 7;
      shiftStartDay = TODAY;
      shiftEndHour = 19;
      shiftEndDay = TODAY;
    } else if (nowHour >= 23 && nowHour < 24) {
      shiftStartHour = 19;
      shiftStartDay = TODAY;
      shiftEndHour = 7;
      shiftEndDay = TOMORROW;
    }

    ZonedDateTime shiftStart = ZonedDateTime.of(
        now.getYear(),
        now.getMonthValue(),
        now.getDayOfMonth(),
        shiftStartHour,
        0,
        0,
        0,
        clock.getZone());
    shiftStart = shiftStart.plusDays(shiftStartDay);

    ZonedDateTime shiftEnd = ZonedDateTime.of(
        now.getYear(),
        now.getMonthValue(),
        now.getDayOfMonth(),
        shiftEndHour,
        0,
        0,
        0,
        clock.getZone());
    shiftEnd = shiftEnd.plusDays(shiftEndDay);

    PeriodDt shift = new PeriodDt();
    shift.setStart(new DateTimeDt(Date.from(shiftStart.toInstant())));
    shift.setEnd(new DateTimeDt(Date.from(shiftEnd.toInstant())));

    return shift;
  }

  /**
   * Gets period from the start of the current or prior shift to now.
   *
   * @param clock
   *     clock with the relevant time zone
   * @return period from the start of the current or prior shift to now
   */
  public static PeriodDt getCurrentOrPriorShiftToNow(Clock clock) {
    PeriodDt shift = getCurrentOrPriorShift(clock);
    shift.setEnd(new DateTimeDt(Date.from(Instant.now(clock))));

    return shift;
  }

  /**
   * Gets period from midnight to now.
   *
   * @param clock
   *     clock with the relevant time zone
   * @return period from midnight to now.
   */
  public static PeriodDt getMidnightToNow(Clock clock) {
    ZonedDateTime now = ZonedDateTime.now(clock);
    ZonedDateTime midnight = ZonedDateTime.of(
        now.getYear(),
        now.getMonthValue(),
        now.getDayOfMonth(),
        0,
        0,
        0,
        0,
        clock.getZone());
    PeriodDt sinceMidnight = new PeriodDt();
    sinceMidnight.setStart(new DateTimeDt(Date.from(midnight.toInstant())));
    sinceMidnight.setEnd(new DateTimeDt(Date.from(now.toInstant())));
    return sinceMidnight;
  }
}
