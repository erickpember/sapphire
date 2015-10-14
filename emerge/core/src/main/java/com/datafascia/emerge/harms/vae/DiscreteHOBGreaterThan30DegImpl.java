// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.composite.PeriodDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.ProcedureRequestUtils;
import com.datafascia.emerge.ucsf.codes.MaybeEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * VAE Harm HOB greater than or equal to 30 degrees Implementation
 */
@Slf4j
public class DiscreteHOBGreaterThan30DegImpl {
  // Private constructor disallows creating instances of this class.
  private DiscreteHOBGreaterThan30DegImpl() {
  }

  @Inject
  private ClientBuilder apiClient;

  private static final String DEFAULT_RESULT = MaybeEnum.NOT_DOCUMENTED.getCode();
  private static final List<String> NON_MED_BED_ORDER_CODES = Arrays.asList("HOB Flat", "Prone",
      "Supine", "Lie Flat", "HOB <10", "HOB <30", "Bed Rest with HOB <=30 Degrees",
      "Bed Rest with HOB Flat");

  /**
   * VAE Harm HOB greater than or equal to 30 degrees Implementation
   * Returns whether the encounter contains an Observation that indicates bed height over 30 deg.
   *
   * @param encounterId
   *    The encounter to check.
   * @return
   *    "Yes", "No", "Not Documented" or "Contraindicated" depending on the answer to the question.
   */
  public String getHobOver30(String encounterId) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.MINUTE, -15);
    Date fifteenMinutesAgo = cal.getTime();
    cal.add(Calendar.MINUTE, -45);
    Date oneHourAgo = cal.getTime();
    cal.add(Calendar.MINUTE, -15);
    Date seventyFiveMinutesAgo = cal.getTime();
    PeriodDt betweenSeventyFiveAndFifteenMinutesAgo = new PeriodDt();
    betweenSeventyFiveAndFifteenMinutesAgo.setStart(new DateTimeDt(seventyFiveMinutesAgo));
    betweenSeventyFiveAndFifteenMinutesAgo.setEnd(new DateTimeDt(fifteenMinutesAgo));

    Observation freshestDiscreteHOB = ObservationUtils.getFreshestByCodeAfterTime(apiClient,
        encounterId, ObservationCodeEnum.HEIGHT_OF_BED.getCode(), oneHourAgo);

    if (freshestDiscreteHOB != null) {
      switch (freshestDiscreteHOB.getValue().toString()) {
        case "HOB 30":
        case "HOB 45":
        case "HOB 60":
        case "HOB 90":
          if (!ObservationUtils.isAfter(freshestDiscreteHOB, fifteenMinutesAgo)) {
            return MaybeEnum.YES.getCode();
          }
        case "HOB Flat":
        case "HOB less than 20":
          break;
        default:
          log.warn("Unexpected value for height of bed value in observation found: "
              + freshestDiscreteHOB.getValue().toString());
      }
    }

    Observation freshestDiscreteHOBFromPast75Minutes = ObservationUtils
        .getFreshestByCodeInTimeFrame(apiClient, encounterId, ObservationCodeEnum.HEIGHT_OF_BED
            .getCode(), betweenSeventyFiveAndFifteenMinutesAgo);

    if (freshestDiscreteHOBFromPast75Minutes != null) {
      switch (freshestDiscreteHOBFromPast75Minutes.getValue().toString()) {
        case "HOB 30":
        case "HOB 45":
        case "HOB 60":
        case "HOB 90":
          break;
        case "HOB Flat":
        case "HOB less than 20":
          return MaybeEnum.NO.getCode();
        default:
          log.warn("Unexpected value for height of bed value in observation found: "
              + freshestDiscreteHOBFromPast75Minutes.getValue().toString());
      }
    }

    if (apiClient.getProcedureRequestClient().getProcedureRequest(encounterId).stream()
        .filter(request -> NON_MED_BED_ORDER_CODES.contains(request.getCode().getCodingFirstRep()
                .getCode())).anyMatch(request -> ProcedureRequestUtils.beforeNow(request))) {
      return MaybeEnum.CONTRAINDICATED.getCode();
    }

    return DEFAULT_RESULT;
  }
}
