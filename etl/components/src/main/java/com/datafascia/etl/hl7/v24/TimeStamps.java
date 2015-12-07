// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.fhir.model.api.TemporalPrecisionEnum;
import ca.uhn.fhir.model.primitive.DateDt;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.datatype.TS;
import ca.uhn.hl7v2.model.v24.datatype.TSComponentOne;

/**
 * Converts HL7 time stamp to FHIR date time.
 */
public class TimeStamps {

  // Private constructor disallows creating instances of this class.
  private TimeStamps() {
  }

  /**
   * Converts HL7 time stamp to FHIR date.
   *
   * @param ts
   *     HL7 time stamp
   * @return FHIR date, or {@code null} if time stamp is null
   * @throws HL7Exception if HL7 message is malformed
   */
  public static DateDt toDate(TS ts) throws HL7Exception {
    TSComponentOne fromTime = ts.getTimeOfAnEvent();
    if (fromTime.isEmpty()) {
      return null;
    }

    return new DateDt(fromTime.getValueAsDate());
  }

  /**
   * Converts HL7 time stamp to FHIR date time.
   *
   * @param ts
   *     HL7 time stamp
   * @return FHIR date time, or {@code null} if time stamp is null
   * @throws HL7Exception if HL7 message is malformed
   */
  public static DateTimeDt toDateTime(TS ts) throws HL7Exception {
    TSComponentOne fromTime = ts.getTimeOfAnEvent();
    if (fromTime.isEmpty()) {
      return null;
    }

    return new DateTimeDt(fromTime.getValueAsDate(), TemporalPrecisionEnum.SECOND);
  }
}
