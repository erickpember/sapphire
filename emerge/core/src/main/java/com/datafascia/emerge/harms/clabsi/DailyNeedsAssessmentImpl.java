// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.clabsi;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Procedure;
import ca.uhn.fhir.model.dstu2.valueset.ProcedureStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import com.datafascia.emerge.ucsf.codes.ProcedureCategoryEnum;
import java.time.Clock;
import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;

/**
 * Checks if daily needs assessment was completed.
 */
public class DailyNeedsAssessmentImpl {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  private static boolean isCentralLine(Procedure procedure) {
    return procedure.getCategory().getCodingFirstRep().getCode()
        .equals(ProcedureCategoryEnum.CENTRAL_LINE.getCode());
  }

  private boolean haveActiveCentralLine(String encounterId) {
    List<Procedure> procedures = apiClient.getProcedureClient().searchProcedure(
        encounterId, null, ProcedureStatusEnum.IN_PROGRESS.getCode());
    return procedures.stream()
        .anyMatch(procedure -> isCentralLine(procedure));
  }

  /**
   * Checks if daily needs assessment was completed.
   *
   * @param encounterId
   *     encounter to search
   * @return {@code "Yes"}, {@code "No"} or {@code "N/A"}
   */
  public String test(String encounterId) {
    if (!haveActiveCentralLine(encounterId)) {
      return "N/A";
    }

    List<Observation> observations = apiClient.getObservationClient().searchObservation(
        encounterId, ObservationCodeEnum.NEEDS_ASSESSMENT.getCode(), null);
    Observation freshestCVCNeedAssessment = ObservationUtils.findFreshestObservation(observations);

    ZonedDateTime now = ZonedDateTime.now(clock);
    Instant sevenAmTodayInstant = ZonedDateTime.of(
        now.getYear(),
        now.getMonthValue(),
        now.getDayOfMonth(),
        7,
        0,
        0,
        0,
        now.getZone())
        .toInstant();
    Date sevenAmToday = Date.from(sevenAmTodayInstant);
    Date sevenAmYesterday = Date.from(sevenAmTodayInstant.minus(24, ChronoUnit.HOURS));

    int hourOftheDay = now.getHour();
    if (hourOftheDay >= 7 && freshestCVCNeedAssessment.getValue() != null) {
      if (freshestCVCNeedAssessment.getValue().toString().equals("Completed")) {
        return "Yes";
      }
      if (ObservationUtils.getEffectiveDate(freshestCVCNeedAssessment).before(sevenAmToday)) {
        return "No";
      }
    } else {
      if (freshestCVCNeedAssessment.getValue().toString().equals("Completed")
          && ObservationUtils.getEffectiveDate(freshestCVCNeedAssessment).after(sevenAmYesterday)) {
        return "Yes";
      }
      if (ObservationUtils.getEffectiveDate(freshestCVCNeedAssessment).before(sevenAmYesterday)) {
        return "No";
      }
    }

    return "No";
  }
}
