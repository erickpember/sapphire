// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.respectdignity;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.valueset.FlagStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.FlagUtils;
import java.util.List;

/**
 * Patient Care Conference Note Timestamp Implementation
 */
public class PatientCareConferenceNoteTimestamp {
  private static final String CODE = "10000002";

  /**
   * Patient Care Conference Note Timestamp Implementation
   *
   * @param client
   *     API client
   * @param patientId
   *     patient to search
   * @return
   *     Patient care conference note timestamp.
   **/
  public static DateTimeDt patientCareConferenceNoteTimestamp(ClientBuilder client,
      String patientId) {
    List<Flag> flags = client.getFlagClient().searchFlag(patientId, CODE, null);
    Flag freshestFlag = FlagUtils.findFreshestFlag(flags);

    if (freshestFlag != null &&
        freshestFlag.getStatusElement().getValueAsEnum() == FlagStatusEnum.ACTIVE) {
      return freshestFlag.getPeriod().getStartElement();
    } else {
      return null;
    }
  }
}
