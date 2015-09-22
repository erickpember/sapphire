// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.aog;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.valueset.FlagStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.domain.fhir.FlagCodeEnum;
import com.datafascia.emerge.ucsf.FlagUtils;
import java.util.List;

/**
 * Patient Care Conference Note Timestamp Implementation
 */
public class PatientCareConferenceNote {
  /**
   * Patient Care Conference Note Implementation
   *
   * @param client
   *     API client
   * @param patientId
   *     patient to search
   * @return
   *     true if active Patient Care Conference Note, otherwise false
   **/
  public static boolean patientCareConferenceNote(ClientBuilder client,
      String patientId) {
    List<Flag> flags = client.getFlagClient().searchFlag(patientId,
        FlagCodeEnum.PATIENT_CARE_CONFERENCE_NOTE.getCode(), null);
    Flag freshestFlag = FlagUtils.findFreshestFlag(flags);

    if (freshestFlag != null &&
        freshestFlag.getStatusElement().getValueAsEnum() == FlagStatusEnum.ACTIVE) {
      return true;
    } else {
      return false;
    }
  }
}
