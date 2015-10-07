// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.harms.aog;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.valueset.FlagStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import com.datafascia.api.client.ClientBuilder;
import com.datafascia.emerge.ucsf.FlagUtils;
import com.datafascia.emerge.ucsf.PatientCareConferenceNote;
import com.datafascia.emerge.ucsf.codes.FlagCodeEnum;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Patient Care Conference Note Timestamp Implementation
 */
public class PatientCareConferenceNoteImpl {
  private static final String CODE = "10000002";

  /**
   * Patient Care Conference Note Implementation
   *
   * @param client
   *     API client
   * @param patientId
   *     patient to search
   * @return
   *     PatientCareConferenceNote object if active Patient Care Conference Note,
   *     otherwise false
   **/
  public static Optional<PatientCareConferenceNote> patientCareConferenceNote(
      ClientBuilder client, String patientId) {
    PatientCareConferenceNote pccn = new PatientCareConferenceNote();

    Optional<Boolean> value = getValue(client, patientId);
    if (value.isPresent()) {
      pccn.setValue(value.get());
    }

    Optional<DateTimeDt> timestamp = getTimestamp(client, patientId);
    if (timestamp.isPresent()) {
      Date ts = new Date(timestamp.get().getValueAsString());
      pccn.setPatientCareConferenceNoteTime(ts);
    }

    if (value.isPresent() && timestamp.isPresent()) {
      return Optional.of(pccn);
    }

    return Optional.empty();
  }


  /**
   * Retrieve the Patient Conference Care Note value
   *
   * @param client
   *     API client
   * @param patientId
   *     patient to search
   * @return
   *     true if active Patient Care Conference Note, otherwise false
   **/
  private static Optional<Boolean> getValue(ClientBuilder client, String patientId) {
    List<Flag> flags = client.getFlagClient().searchFlag(patientId,
        FlagCodeEnum.PATIENT_CARE_CONFERENCE_NOTE.getCode(), null);
    Flag freshestFlag = FlagUtils.findFreshestFlag(flags);
    Optional<Boolean> noteValue;

    if (freshestFlag == null) {
      return Optional.empty();
    } else if (freshestFlag.getStatusElement().getValueAsEnum() == FlagStatusEnum.ACTIVE) {
      return Optional.of(Boolean.valueOf(true));
    } else {
      return Optional.of(Boolean.valueOf(false));
    }
  }


  /**
   * Retrieve the Patient Conference Care Note timestamp
   *
   * @param client
   *     API client
   * @param patientId
   *     patient to search
   * @return
   *     Patient care conference note timestamp if it exists, otherwise return empty
   **/
  private static Optional<DateTimeDt> getTimestamp(ClientBuilder client,
      String patientId) {
    List<Flag> flags = client.getFlagClient().searchFlag(patientId, CODE, null);
    Flag freshestFlag = FlagUtils.findFreshestFlag(flags);

    if (freshestFlag == null) {
      return Optional.empty();
    } else if (freshestFlag.getStatusElement().getValueAsEnum() == FlagStatusEnum.ACTIVE) {
      return Optional.of(freshestFlag.getPeriod().getStartElement());
    } else {
      return Optional.empty();
    }
  }
}
