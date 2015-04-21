// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.model.v24.segment.PV1;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.PatientData;

/**
 * Converts ADT HL7 message data to event data.
 */
public abstract class AdmitPatientTransformer extends BaseTransformer {

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
        .build();
  }

  protected AdmitPatientData toAdmitPatientData(PID pid, PV1 pv1) throws HL7Exception {
    return AdmitPatientData.builder()
        .patient(toPatientData(pid))
        .encounter(toEncounterData(pv1))
        .build();
  }
}
