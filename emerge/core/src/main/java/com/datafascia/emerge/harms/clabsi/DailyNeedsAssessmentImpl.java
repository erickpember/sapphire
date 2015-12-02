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
import java.util.Optional;
import javax.inject.Inject;

/**
 * Checks if daily needs assessment was completed.
 */
public class DailyNeedsAssessmentImpl {

  @Inject
  private Clock clock;

  @Inject
  private ClientBuilder apiClient;

  /**
   * Checks if observation is relevant to Verbal Pain.
   *
   * @param observation
   *     the observation to check
   * @return true if observation is relevant to Verbal Pain.
   */
  public static boolean isRelevant(Observation observation) {
    return ObservationCodeEnum.NEEDS_ASSESSMENT.isCodeEquals(observation.getCode());
  }

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

    Optional<Observation> freshestCVCNeedAssessment = ObservationUtils
        .getFreshestByCodeAfterTime(
            apiClient,
            encounterId,
            ObservationCodeEnum.NEEDS_ASSESSMENT.getCode(),
            sevenAmYesterday);

    if (!freshestCVCNeedAssessment.isPresent() ||
        freshestCVCNeedAssessment.get().getValue() == null) {
      return "No";
    }

    int hourOftheDay = now.getHour();
    if (hourOftheDay >= 7) {
      if (ObservationUtils.getEffectiveDate(freshestCVCNeedAssessment.get()).before(sevenAmToday)) {
        return "No";
      } else if (freshestCVCNeedAssessment.get().getValue().toString().equals("Completed")) {
        return "Yes";
      }
    } else {
      if (freshestCVCNeedAssessment.get().getValue().toString().equals("Completed")
          && ObservationUtils.getEffectiveDate(freshestCVCNeedAssessment.get()).after(
              sevenAmYesterday)) {
        return "Yes";
      }
      if (ObservationUtils.getEffectiveDate(freshestCVCNeedAssessment.get())
          .before(sevenAmYesterday)) {
        return "No";
      }
    }

    return "No";
  }
}
