// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.clabsi;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.google.inject.Inject;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Utilities related to Daily Needs
 */
public class DailyNeeds {
  @Inject
  private static ClientBuilder client;

  /**
   * Returns whether daily needs have been completed.
   *
   * @param encounterId The encounter to check.
   * @return Whether daily needs have been completed.
   */
  public static String dailyNeedsAssessment(String encounterId) {
    boolean activeLine = checkActiveLines(encounterId);

    if (!activeLine) {
      return "N/A";
    }

    List<Observation> observations = client.getObservationClient().searchObservation(encounterId,
        null, null);

    Observation freshestCVCNeedAssessment = ObservationUtils.findFreshestObservation(observations);

    Calendar cal = Calendar.getInstance();
    cal.set(Calendar.HOUR_OF_DAY, 7);
    Date sevenAmToday = cal.getTime();

    cal.add(Calendar.HOUR, -24);
    Date sevenAmYesterday = cal.getTime();

    int hourOftheDay = cal.get(Calendar.HOUR_OF_DAY);

    if (hourOftheDay >= 7) {
      if (freshestCVCNeedAssessment.getValue().toString().equals("Completed")) {
        return "Completed";
      }
      if (ObservationUtils.getEffectiveDate(freshestCVCNeedAssessment).before(sevenAmToday)) {
        return "Not Completed";
      }
    } else {
      if (freshestCVCNeedAssessment.getValue().toString().equals("Completed")
          && ObservationUtils.getEffectiveDate(freshestCVCNeedAssessment).after(sevenAmYesterday)) {
        return "Completed";
      }
      if (ObservationUtils.getEffectiveDate(freshestCVCNeedAssessment).before(sevenAmYesterday)) {
        return "Not Completed";
      }
    }

    return null;
  }

  private static boolean checkActiveLines(String encounterId) {
    List<Procedure> procedures
        = client.getProcedureClient().searchProcedure(encounterId, null, "IN_PROGRESS");
    return !procedures.isEmpty();
  }
}
