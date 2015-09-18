// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.InstantDt;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * Date utility methods
 */
public class Dates {

  private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");

  // Private constructor disallows creating instances of this class.
  private Dates() {
  }

  /**
   * Converts Java {@link LocalDate} to FHIR date with day precision.
   *
   * @param localDate
   *     convert from
   * @return FHIR date
   */
  public static DateDt toDate(LocalDate localDate) {
    ZonedDateTime dateTime = ZonedDateTime.of(localDate, LocalTime.MIDNIGHT, TIME_ZONE);
    return new DateDt(Date.from(dateTime.toInstant()), TemporalPrecisionEnum.DAY);
  }

  /**
   * Converts Java {@link LocalDate} and {@link LocalTime} to FHIR date time with second precision.
   *
   * @param localDate
   *     convert from
   * @param localTime
   *     convert from
   * @return FHIR date
   */
  public static DateTimeDt toDateTime(LocalDate localDate, LocalTime localTime) {
    ZonedDateTime dateTime = ZonedDateTime.of(localDate, localTime, TIME_ZONE);
    return new DateTimeDt(Date.from(dateTime.toInstant()), TemporalPrecisionEnum.SECOND);
  }

  /**
   * Converts Java {@link Instant} to FHIR date time with second precision.
   *
   * @param instant
   *     convert from
   * @return FHIR date
   */
  public static DateTimeDt toDateTime(Instant instant) {
    if (instant == null) {
      return null;
    }

    return new DateTimeDt(Date.from(instant), TemporalPrecisionEnum.SECOND);
  }

  /**
   * Converts Java {@link Instant} to FHIR instant with second precision.
   *
   * @param instant
   *     convert from
   * @return FHIR date
   */
  public static InstantDt toInstant(Instant instant) {
    if (instant == null) {
      return null;
    }

    return new InstantDt(Date.from(instant), TemporalPrecisionEnum.SECOND);
  }
}
