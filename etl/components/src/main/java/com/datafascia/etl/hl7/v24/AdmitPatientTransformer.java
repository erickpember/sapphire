// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.model.v24.segment.PV1;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.domain.event.AddObservationsData;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import com.datafascia.domain.event.ObservationType;
import com.datafascia.domain.event.PatientData;
import com.google.common.base.Strings;
import java.net.URI;
import java.util.Optional;

/**
 * Converts ADT HL7 message data to event data.
 */
public abstract class AdmitPatientTransformer extends BaseTransformer {

  private static final String OBX_ROOT_PATH = "/OBX" + SUBSCRIPT_PLACEHOLDER;

  private PatientData toPatientData(PID pid) throws HL7Exception {
    XPN patientName = pid.getPatientName(0);
    return PatientData.builder()
        .institutionPatientId(
            pid.getPatientIdentifierList(0).getID().getValue())
        .accountNumber(
            pid.getPatientAccountNumber().getID().getValue())
        .firstName(
            patientName.getGivenName().getValueOrEmpty())
        .middleName(
            patientName.getSecondAndFurtherGivenNamesOrInitialsThereof().getValueOrEmpty())
        .lastName(
            patientName.getFamilyName().getSurname().getValueOrEmpty())
        .gender(
            toGender(pid.getAdministrativeSex().getValue()))
        .birthDate(
            toLocalDate(pid.getDateTimeOfBirth().getTimeOfAnEvent()))
        .maritalStatus(
            toMaritalStatus(pid.getMaritalStatus().getIdentifier().getValue()))
        .race(
            toRace(pid.getRace(0).getIdentifier().getValue()))
        .language(
            toLanguage(pid.getPrimaryLanguage().getIdentifier().getValue()))
        .build();
  }

  private EncounterData toEncounterData(PV1 pv1) throws HL7Exception {
    return EncounterData.builder()
        .identifier(
            pv1.getVisitNumber().getID().getValue())
        .admitTime(
            toInstant(pv1.getAdmitDateTime().getTimeOfAnEvent()))
        .dischargeTime(
            toInstant(pv1.getDischargeDateTime(0).getTimeOfAnEvent()))
        .build();
  }

  protected AdmitPatientData toAdmitPatientData(PID pid, PV1 pv1) throws HL7Exception {
    return AdmitPatientData.builder()
        .patient(toPatientData(pid))
        .encounter(toEncounterData(pv1))
        .build();
  }

  /**
   * Transforms any OBX segments in the HL7 message to an add observations event.
   *
   * @param input
   *     HL7 message
   * @param pid
   *     PID segment
   * @param pv1
   *     PV1 segement
   * @param institutionId
   *     institution ID
   * @param facilityId
   *     facility ID
   * @param trigger
   *     HL7 trigger
   * @return optional event, empty if no OBX segments found
   * @throws HL7Exception if the segment could not be obtained
   */
  protected Optional<Event> toAddObservationsEvent(
      Message input, PID pid, PV1 pv1, URI institutionId, URI facilityId, ObservationType trigger)
          throws HL7Exception {

    // See if OBX segment exists.
    Terser terser = new Terser(input);
    if (Strings.isNullOrEmpty(terser.get(OBX_ROOT_PATH.replace(SUBSCRIPT_PLACEHOLDER, "")
        + "-1"))) {
      return Optional.empty();
    }

    AddObservationsData addObservationsData = toAddObservationsData(
        pid,
        pv1,
        OBX_ROOT_PATH,
        "",
        terser,
        trigger);
    return Optional.of(Event.builder()
        .institutionId(institutionId)
        .facilityId(facilityId)
        .type(EventType.OBSERVATIONS_ADD)
        .data(addObservationsData)
        .build());
  }
}
