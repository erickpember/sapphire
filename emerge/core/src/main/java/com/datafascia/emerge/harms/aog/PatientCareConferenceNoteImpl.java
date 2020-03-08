// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
