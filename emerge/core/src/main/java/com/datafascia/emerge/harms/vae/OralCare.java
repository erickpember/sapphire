// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.vae;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.primitive.StringDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.ObservationUtils;
import com.datafascia.emerge.ucsf.codes.MaybeEnum;
import com.datafascia.emerge.ucsf.codes.ObservationCodeEnum;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * VAE Harm Oral Care Implementation
 */
@Slf4j
public class OralCare {
  @Inject
  private ClientBuilder apiClient;

  private static final String DEFAULT_RESULT = MaybeEnum.NOT_DOCUMENTED.getCode();
  private static final List<String> YES_VALUES = Arrays.asList("Teeth brushed with CHG",
      "Suction toothette with H. Peroxide", "Mouth swabbed", "Teeth brushed");
  private static final List<String> NO_VALUES = Arrays.asList("Patient refused", "Other (Comment)");
  private static final List<String> CONTRAINDICATED_VALUES = Arrays.asList(
      "Contraindicated (bleeding, “no oral care” order)",
      "Patient unavailable (off unit, procedure in progress)");

  /**
   * VAE Harm Oral Care Implementation
   * Returns whether the encounter contains an Observation that indicates oral care in the last 7
   * hours.
   *
   * @param encounterId
   *    The encounter to check.
   * @return
   *    "Yes", "No", "Not Documented" or "Contraindicated" depending on the answer to the question.
   */
  public String getOralCare(String encounterId) {
    Calendar cal = Calendar.getInstance();
    cal.add(Calendar.HOUR, -7);
    Date sevenHoursAgo = cal.getTime();

    Observation freshestOralCareAction = ObservationUtils.getFreshestByCodeAfterTime(apiClient,
        encounterId, ObservationCodeEnum.ORAL_CARE.getCode(), sevenHoursAgo);

    if (freshestOralCareAction != null) {
      if (YES_VALUES.contains(((StringDt) freshestOralCareAction.getValue()).getValue())) {
        return MaybeEnum.YES.getCode();
      } else if (NO_VALUES.contains(((StringDt) freshestOralCareAction.getValue()).getValue())) {
        return MaybeEnum.NO.getCode();
      } else if (CONTRAINDICATED_VALUES.contains(((StringDt) freshestOralCareAction.getValue())
          .getValue())) {
        return MaybeEnum.CONTRAINDICATED.getCode();
      } else {
        log.warn("Unrecognized value for oral care observation: " + freshestOralCareAction
            .getValue());
      }
    }

    return DEFAULT_RESULT;
  }
}
