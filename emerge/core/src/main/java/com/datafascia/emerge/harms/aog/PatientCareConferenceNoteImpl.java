// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.aog;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.valueset.FlagStatusEnum;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.FlagUtils;
import com.datafascia.emerge.ucsf.PatientCareConferenceNote;
import com.datafascia.emerge.ucsf.codes.FlagCodeEnum;
import java.util.List;
import java.util.Optional;
import javax.inject.Inject;

/**
 * Patient Care Conference Note implementation
 */
public class PatientCareConferenceNoteImpl {

  @Inject
  private ClientBuilder apiClient;

  /**
   * Finds patient care conference note for a patient.
   *
   * @param patientId
   *     patient to search
   * @return PatientCareConferenceNote if found, otherwise empty
   **/
  public Optional<PatientCareConferenceNote> findPatientCareConferenceNote(String patientId) {
    List<Flag> flags = apiClient.getFlagClient()
        .searchFlag(patientId, FlagCodeEnum.PATIENT_CARE_CONFERENCE_NOTE.getCode(), null);
    if (flags.isEmpty()) {
      return Optional.empty();
    }

    Flag freshestFlag = FlagUtils.findFreshestFlag(flags);

    PatientCareConferenceNote note = new PatientCareConferenceNote()
        .withValue(
            freshestFlag.getStatusElement().getValueAsEnum() == FlagStatusEnum.ACTIVE)
        .withPatientCareConferenceNoteTime(
            freshestFlag.getPeriod().getStart());
    return Optional.of(note);
  }
}
