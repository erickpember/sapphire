// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.v24.datatype.CE;
import ca.uhn.hl7v2.model.v24.datatype.FT;
import ca.uhn.hl7v2.model.v24.datatype.TSComponentOne;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.segment.NTE;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.model.v24.segment.PV1;
import com.datafascia.domain.event.AdmitData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.ObservationData;
import com.datafascia.domain.event.ObservationType;
import com.datafascia.domain.event.PatientData;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.Race;
import com.datafascia.etl.hl7transform.MessageToEventTransformer;
import com.datafascia.etl.hl7transform.RaceMap;
import com.neovisionaries.i18n.LanguageAlpha3Code;
import com.neovisionaries.i18n.LanguageCode;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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

  /**
   * Takes an OBX and it's associated NTEs and converts it into an Avro-friendly POJO.
   * @param obx The OBX segment.
   * @param ntes The NTE segment.
   * @param observationType The type of message it came from.
   * @return Avro-friendly Pojo.
   * @throws HL7Exception
   */
  protected ObservationData toObservationData(OBX obx, List<NTE> ntes,
                                              ObservationType observationType) throws HL7Exception {
    List<String> observationMethod = new ArrayList<>();
    for (CE ce : obx.getObservationMethod()) {
      observationMethod.add(ce.getText().encode());
    }

    List<String> comments = new ArrayList<>();
    if (ntes != null) {
      for (NTE nte : ntes) {
        for (FT ft : nte.getComment()) {
          comments.add(ft.encode());
        }
      }
    }

    List<String> abnormalFlags = null;
    if (!obx.getAbnormalFlags().isEmpty()) {
      abnormalFlags.add(obx.getAbnormalFlags().encode());
    }

    String probability = null;
    if (obx.getProbability().length > 0) {
      probability = obx.getProbability()[0].encode();
    }
    String observationValue = null;
    if (obx.getObservationValue().length > 0) {
      observationValue = obx.getObservationValue()[0].encode();
    }
    return ObservationData.builder()
        .observationMethod(observationMethod)
        .abnormalFlags(abnormalFlags)
        .comments(comments)
        .natureOfAbnormalTest(obx.getNatureOfAbnormalTest().encode())
        .effectiveDateOfLastNormalObservation(
            obx.getDateLastObservationNormalValue().getTimeOfAnEvent().encode())
        .userDefinedAccessChecks(obx.getUserDefinedAccessChecks().encode())
        .observationDateAndTime(obx.getDateTimeOfTheObservation().getTimeOfAnEvent().encode())
        .responsibleObserver(obx.getResponsibleObserver().encode())
        .resultStatus(obx.getObservationResultStatus().getValueOrEmpty())
        .probability(probability)
        .producersId(obx.getProducerSID().encode())
        .valueUnits(obx.getUnits().encode())
        .valueType(obx.getValueType().getValueOrEmpty())
        .subId(obx.getObservationSubId().getValueOrEmpty())
        .value(observationValue)
        .id(obx.getSetIDOBX().encode())
        .referenceRange(obx.getReferencesRange().encode())
        .observationType(observationType).build();
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
    if (!RaceMap.raceMap.containsKey(code.toLowerCase())) {
      return Race.UNKNOWN;
    }
    return RaceMap.raceMap.get(code.toLowerCase());
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
