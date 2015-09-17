// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.fhir.model.dstu2.valueset.EncounterStateEnum;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.segment.MSH;
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
import com.datafascia.etl.hl7.GenderFormatter;
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
            GenderFormatter.parse(pid.getAdministrativeSex().getValue()))
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

  private static String toEncounterState(String triggerEvent) {
    switch (triggerEvent) {
      case "A01":
      case "A06":
      case "A07":
      case "A08":
      case "A12":
      case "A13":
      case "A17":
        return EncounterStateEnum.IN_PROGRESS.getCode();
      case "A03":
        return EncounterStateEnum.FINISHED.getCode();
      case "A04":
        return EncounterStateEnum.ARRIVED.getCode();
      case "A11":
        return EncounterStateEnum.CANCELLED.getCode();
      default:
        return "";
    }
  }

  private EncounterData toEncounterData(MSH msh, PV1 pv1) throws HL7Exception {
    return EncounterData.builder()
        .status(
            toEncounterState(msh.getMessageType().getTriggerEvent().getValue()))
        .identifier(
            pv1.getVisitNumber().getID().getValue())
        .location(
            pv1.getAssignedPatientLocation().encode())
        .admitTime(
            toInstant(pv1.getAdmitDateTime().getTimeOfAnEvent()))
        .dischargeTime(
            toInstant(pv1.getDischargeDateTime(0).getTimeOfAnEvent()))
        .build();
  }

  protected AdmitPatientData toAdmitPatientData(MSH msh, PID pid, PV1 pv1) throws HL7Exception {
    return AdmitPatientData.builder()
        .patient(toPatientData(pid))
        .encounter(toEncounterData(msh, pv1))
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
   *     PV1 segment
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
