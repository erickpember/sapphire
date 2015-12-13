// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.composite.TimingDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import java.time.Instant;
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
   * @param fhirTime
   *     FHIR time, of one of three types
   * @return Java Date
   */
  public static Date toDate(IDatatype fhirTime) {
    if (fhirTime == null) {
      return null;
    } else if (fhirTime instanceof DateTimeDt) {
      return ((DateTimeDt) fhirTime).getValue();
    } else if (fhirTime instanceof PeriodDt) {
      return ((PeriodDt) fhirTime).getStart();
    } else if (fhirTime instanceof TimingDt) {
      return toDate(((TimingDt) fhirTime).getRepeat().getBounds());
    } else {
      throw new IllegalArgumentException("Cannot convert from " + fhirTime);
    }
  }

  /**
   * Converts FHIR IDatatype of DateTimeDt, PeriodDt or TimingDt to Java {@link Instant}.
   *
   * @param fhirTime
   *     FHIR time, of one of three types
   * @return Java Instant
   */
  public static Instant toInstant(IDatatype fhirTime) {
    Date date = toDate(fhirTime);
    return (date != null) ? date.toInstant() : null;
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
