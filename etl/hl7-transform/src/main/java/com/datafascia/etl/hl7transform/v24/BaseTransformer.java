// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.datatype.TSComponentOne;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.model.v24.segment.PV1;
import com.datafascia.domain.event.AdmitData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.PatientData;
import com.datafascia.etl.hl7transform.MessageToEventTransformer;
import com.datafascia.models.Gender;
import com.datafascia.models.MaritalStatus;
import com.datafascia.models.Race;
import com.neovisionaries.i18n.LanguageAlpha3Code;
import com.neovisionaries.i18n.LanguageCode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;

/**
 * Implements common methods for transformers.
 */
public abstract class BaseTransformer implements MessageToEventTransformer {

  private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");

  protected AdmitData toAdmitData(PID pid, PV1 pv1) throws HL7Exception {
    return AdmitData.builder()
        .patient(toPatientData(pid))
        .encounter(toEncounterData(pv1))
        .build();
  }

  private PatientData toPatientData(PID pid) throws HL7Exception {
    XPN patientName = pid.getPatientName(0);
    return PatientData.builder()
        .institutionPatientId(
            pid.getPatientIdentifierList(0).getID().getValue())
        .accountNumber(pid.getPatientAccountNumber().getID().getValue())
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
        .admitTime(
            toInstant(pv1.getAdmitDateTime().getTimeOfAnEvent()))
        .build();
  }

  private Gender toGender(String code) {
    return Gender.of(code).orElse(Gender.UNKNOWN);
  }

  private LocalDate toLocalDate(TSComponentOne fromDate) throws HL7Exception {
    return LocalDate.of(fromDate.getYear(), fromDate.getMonth(), fromDate.getDay());
  }

  private MaritalStatus toMaritalStatus(String code) {
    return MaritalStatus.of(code).orElse(MaritalStatus.UNKNOWN);
  }

  private Race toRace(String code) {
    return Race.of(code).orElse(Race.UNKNOWN);
  }

  private LanguageCode toLanguage(String code) {
    LanguageAlpha3Code language = LanguageAlpha3Code.getByCodeIgnoreCase(code);
    return (language != null) ? language.getAlpha2() : null;
  }

  private Instant toInstant(TSComponentOne fromTime) throws HL7Exception {
    ZonedDateTime zonedDateTime = ZonedDateTime.of(
        fromTime.getYear(), fromTime.getMonth(), fromTime.getDay(),
        fromTime.getHour(), fromTime.getMinute(), fromTime.getSecond(),
        0,
        TIME_ZONE);
    return zonedDateTime.toInstant();
  }
}
