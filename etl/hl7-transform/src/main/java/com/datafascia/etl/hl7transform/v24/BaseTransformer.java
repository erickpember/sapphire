// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v24.datatype.CE;
import ca.uhn.hl7v2.model.v24.datatype.FT;
import ca.uhn.hl7v2.model.v24.datatype.TSComponentOne;
import ca.uhn.hl7v2.model.v24.datatype.XPN;
import ca.uhn.hl7v2.model.v24.segment.NTE;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.model.v24.segment.PV1;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.domain.event.AddObservationsData;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.EncounterData;
import com.datafascia.domain.event.ObservationData;
import com.datafascia.domain.event.ObservationType;
import com.datafascia.domain.event.PatientData;
import com.datafascia.domain.model.Gender;
import com.datafascia.domain.model.MaritalStatus;
import com.datafascia.domain.model.Race;
import com.datafascia.etl.hl7transform.MessageToEventTransformer;
import com.datafascia.etl.hl7transform.RaceMap;
import com.google.common.base.Strings;
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

  protected static final String SUBSCRIPT_PLACEHOLDER = "REPLACEME";

  protected AdmitPatientData toAdmitData(PID pid, PV1 pv1) throws HL7Exception {
    return AdmitPatientData.builder()
        .patient(toPatientData(pid))
        .encounter(toEncounterData(pv1))
        .build();
  }

  /**
   * Converts segments to appropriate versions of NTE and OBX in order to call
   * and wrap toObservationData.
   *
   * @param obx Segment to convert to OBX
   * @param segmentNotes Segment to convert to List<NTE>
   * @param observationType Type of message from which the aforementioned segments were extracted.
   * @return observation data
   * @throws HL7Exception Parsing error encountered in toObservationData.
   */
  private ObservationData segmentsToObservationData(Segment obx,
      List<Segment> segmentNotes, ObservationType observationType) throws HL7Exception {
    ArrayList<NTE> ntes = new ArrayList<>();
    for (Segment seg : segmentNotes) {
      ntes.add((NTE) seg);
    }

    return toObservationData((OBX) obx, ntes, observationType);
  }

  /**
   * Takes an OBX and it's associated NTEs and converts it into an Avro-friendly POJO.
   * @param obx The OBX segment.
   * @param ntes The NTE segments.
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

    List<String> abnormalFlags = new ArrayList<>();
    if (!obx.getAbnormalFlags().isEmpty()) {
      abnormalFlags.add(obx.getAbnormalFlags().encode());
    }

    String probability = null;
    if (obx.getProbability().length > 0) {
      probability = obx.getProbability()[0].encode();
    }

    List<String> observationValue = new ArrayList<>();
    if (obx.getObservationValue().length > 0) {
      for (Varies varies : obx.getObservationValue()) {
        observationValue.add(varies.encode());
      }
    }

    return ObservationData.builder()
        .observationMethod(observationMethod)
        .abnormalFlags(abnormalFlags)
        .comments(comments)
        .natureOfAbnormalTest(obx.getNatureOfAbnormalTest().encode())
        .effectiveDateOfLastNormalObservation(
            toInstant(obx.getDateLastObservationNormalValue().getTimeOfAnEvent()))
        .userDefinedAccessChecks(obx.getUserDefinedAccessChecks().encode())
        .observationDateAndTime(
            toInstant(obx.getDateTimeOfTheObservation().getTimeOfAnEvent()))
        .responsibleObserver(obx.getResponsibleObserver().encode())
        .resultStatus(obx.getObservationResultStatus().getValueOrEmpty())
        .probability(probability)
        .producersId(obx.getProducerSID().encode())
        .valueUnits(obx.getUnits().encode())
        .valueType(obx.getValueType().getValueOrEmpty())
        .subId(obx.getObservationSubId().getValueOrEmpty())
        .value(observationValue)
        .id(obx.getObservationIdentifier().encode())
        .referenceRange(obx.getReferencesRange().encode())
        .observationType(observationType)
        .build();
  }

  /**
   * Groups of multiple OBX and NTE segments are arranged in Hapi Terser paths this way:
   * OBX, OBX(1), OBX(2)
   * This method produces the number sequence "", "(1)","(2)"..."(n)"
   *
   * @param subscript String representing what comes after OBX or NTE, such as (1).
   * @return the next subscript in order.
   */
  private String incrementSubscript(String subscript) {
    if (subscript.isEmpty()) {
      return "(1)";
    } else {
      // Pull the number out of the parens, increment it and put parens back on.
      return "(" + Integer.toString(Integer.parseInt(subscript.replaceAll("[^\\d.]", "")) + 1)
          + ")";
    }
  }

  /**
   * General purpose utility to parse OBX and NTE segments out of HL7 messages.
   *
   * @param pid PID segment
   * @param pv1 PV1 segment
   * @param obxRootPath Terser path to find OBX, varies between message types.
   * @param nteRootPath Terser path to find NTE, varies between message types.
   * @param terser Like XPath for HL7, extracts segments from parsed HL7 using a path.
   * @param observationType The subtype of the wrapping message, such as AO1.
   * @return EventData subclass containing a list of Observations stored in our internal format.
   * @throws ca.uhn.hl7v2.HL7Exception Failure to parse HL7 with terser.
   */
  protected AddObservationsData toAddObservationsData(
      PID pid,
      PV1 pv1,
      String obxRootPath,
      String nteRootPath,
      Terser terser,
      ObservationType observationType)
        throws HL7Exception {

    String obxSubscript = "";
    String currentObxPath = obxRootPath.replace(SUBSCRIPT_PLACEHOLDER, obxSubscript);
    List<ObservationData> observations = new ArrayList<>();

    // iterate through obx segments: OBX, OBX(1), OBX(2)... OBX(n)
    while (!Strings.isNullOrEmpty(terser.get(currentObxPath + "-1"))) {
      Segment obx = terser.getSegment(currentObxPath);

      // iterate through NTE segments
      List<Segment> notes = new ArrayList<>();
      String nteSubscript = "";
      String currentNtePath = nteRootPath.replace(SUBSCRIPT_PLACEHOLDER, obxSubscript);

      while (!Strings.isNullOrEmpty(currentNtePath) &&
          !Strings.isNullOrEmpty(terser.get(currentNtePath + "-1"))) {

        notes.add(terser.getSegment(currentNtePath));

        nteSubscript = incrementSubscript(nteSubscript);
        currentNtePath = nteRootPath.replace(SUBSCRIPT_PLACEHOLDER, obxSubscript)
            + nteSubscript;
      }
      observations.add(segmentsToObservationData(obx, notes, observationType));

      obxSubscript = incrementSubscript(obxSubscript);
      currentObxPath = obxRootPath.replace(SUBSCRIPT_PLACEHOLDER, obxSubscript);
    }

    return AddObservationsData.builder()
        .institutionPatientId(
            pid.getPatientIdentifierList(0).getID().getValue())
        .encounterIdentifier(
            pv1.getVisitNumber().getID().getValue())
        .observations(observations)
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
        .identifier(
            pv1.getVisitNumber().getID().getValue())
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
    if (fromTime.isEmpty()) {
      return null;
    }

    ZonedDateTime zonedDateTime = ZonedDateTime.of(
        fromTime.getYear(), fromTime.getMonth(), fromTime.getDay(),
        fromTime.getHour(), fromTime.getMinute(), fromTime.getSecond(),
        0,
        TIME_ZONE);
    return zonedDateTime.toInstant();
  }
}
