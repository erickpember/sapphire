// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Comparator;
import java.util.Date;

/**
 * Date utility methods
 */
public class Dates {

  // Private constructor disallows creating instances of this class.
  private Dates() {
  }

  /**
   * Provides null-safe date comparator. Nulls are ordered before non-nulls.
   *
   * @return comparator
   */
  public static Comparator<Date> getDateComparator() {
    return Comparator.nullsFirst(Comparator.naturalOrder());
  }

  /**
   * Converts FHIR IDatatype of DateTimeDt, PeriodDt or TimingDt to Java {@link Date}.
   *
   * @param time
   *     FHIR time, of one of three types
   * @return Java Date
   */
  public static Date toDate(IDatatype time) {
    if (time instanceof TimingDt) {
      return toDate(((TimingDt) time).getRepeat().getBounds());
    } else if (time instanceof PeriodDt) {
      return ((PeriodDt) time).getStart();
    } else if (time instanceof DateTimeDt) {
      return ((DateTimeDt) time).getValue();
    } else {
      throw new IllegalArgumentException("Cannot convert from " + time);
    }
  }

  /**
   * Converts Java {@link LocalDate} and {@link LocalTime} to FHIR date time with second precision.
   *
   * @param localDate
   *     convert from
   * @param localTime
   *     convert from
   * @param zoneId
   *     time zone
   * @return FHIR date
   */
  public static DateTimeDt toDateTime(LocalDate localDate, LocalTime localTime, ZoneId zoneId) {
    ZonedDateTime dateTime = ZonedDateTime.of(localDate, localTime, zoneId);
    return new DateTimeDt(Date.from(dateTime.toInstant()), TemporalPrecisionEnum.SECOND);
  }
}
