// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Encounter;
import java.time.Instant;

/**
 * Encounter helper methods
 */
public class EncounterUtils {

  // Private constructor disallows creating instances of this class.
  private EncounterUtils() {
  }

  /**
   * Gets time when patient was first admitted or transferred into an ICU. If the patient was
   * never in an ICU, then returns the hospital admit time.
   *
   * @param encounter
   *     encounter to pull from
   * @return ICU admit time
   */
  public static Instant getIcuPeriodStart(Encounter encounter) {
    PeriodDt period = (encounter.getLocation().size() > 1)
        ? encounter.getLocation().get(1).getPeriod()
        : encounter.getPeriod();
    return period.getStart().toInstant();
  }
}
