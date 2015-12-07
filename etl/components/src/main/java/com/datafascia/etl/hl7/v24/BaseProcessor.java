// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Observation.ReferenceRange;
import ca.uhn.fhir.model.dstu2.valueset.MaritalStatusCodesEnum;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.DecimalDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v24.datatype.CE;
import ca.uhn.hl7v2.model.v24.datatype.FT;
import ca.uhn.hl7v2.model.v24.datatype.TSComponentOne;
import ca.uhn.hl7v2.model.v24.segment.NTE;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.model.v24.segment.PID;
import ca.uhn.hl7v2.model.v24.segment.PV1;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.datafascia.domain.fhir.RaceEnum;
import com.datafascia.etl.hl7.MessageProcessor;
import com.datafascia.etl.hl7.RaceMap;
import com.google.common.base.MoreObjects;
import com.google.common.base.Strings;
import com.neovisionaries.i18n.LanguageCode;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/**
 * Implements common methods for message processors.
 */
public abstract class BaseProcessor implements MessageProcessor {

  protected static final String SUBSCRIPT_PLACEHOLDER = "REPLACEME";

  protected static String getPatientIdentifier(PID pid) {
    String patientIdentifier = pid.getPatientIdentifierList(0).getID().getValue();
    if (Strings.isNullOrEmpty(patientIdentifier)) {
      throw new IllegalStateException("Field PID-3 does not contain patient identifier");
    }
    return patientIdentifier;
  }

  protected static String getEncounterIdentifier(PV1 pv1) {
    String encounterIdentifier = pv1.getVisitNumber().getID().getValue();
    if (Strings.isNullOrEmpty(encounterIdentifier)) {
      throw new IllegalStateException("Field PV1-19 does not contain visit number");
    }
    return encounterIdentifier;
  }

  protected static CodeableConceptDt toCodeableConcept(CE ce) throws HL7Exception {
    if (ce.isEmpty()) {
      return null;
    }

    CodeableConceptDt codeableConcept = new CodeableConceptDt(
        ce.getNameOfCodingSystem().getValue(), ce.getIdentifier().getValue());
    codeableConcept.setText(ce.getText().getValue());
    return codeableConcept;
  }

  private static ObservationStatusEnum toObservationStatus(String resultStatus) {
    if (resultStatus == null) {
      return null;
    }

    switch (resultStatus) {
      case "C":
        return ObservationStatusEnum.AMENDED;
      case "D":
        return ObservationStatusEnum.CANCELLED;
      case "F":
      case "U":
        return ObservationStatusEnum.FINAL;
      case "I":
        return ObservationStatusEnum.REGISTERED;
      case "P":
      case "R":
      case "S":
        return ObservationStatusEnum.PRELIMINARY;
      case "W":
        return ObservationStatusEnum.ENTERED_IN_ERROR;
      default:
        return ObservationStatusEnum.UNKNOWN_STATUS;
    }
  }

  /**
   * Transforms OBX and associated NTE's to observation.
   *
   * @param obx
   *     The OBX segment
   * @param ntes
   *     The NTE segments
   * @return observation
   * @throws HL7Exception if HL7 message is malformed
   */
  protected Observation toObservation(OBX obx, List<NTE> ntes) throws HL7Exception {
    String value = obx.getObservationValue(0).encode();
    IDatatype observationValue;
    switch (obx.getValueType().getValue()) {
      case "NM":
        observationValue = new QuantityDt()
            .setValue(new DecimalDt(value))
            .setUnit(obx.getUnits().encode());
        break;
      default:
        observationValue = new StringDt(value);
        break;
    }

    DateTimeDt effective = TimeStamps.toDateTime(obx.getDateTimeOfTheObservation());
    if (effective == null) {
      effective = new DateTimeDt(new Date());
    }

    Observation observation = new Observation()
        .setCode(
            toCodeableConcept(obx.getObservationIdentifier()))
        .setValue(
            observationValue)
        .setEffective(
            effective)
        .setStatus(
            toObservationStatus(obx.getObservationResultStatus().getValue()))
        .setMethod(
            toCodeableConcept(obx.getObservationMethod(0)));

    observation.addIdentifier()
        .setSystem(IdentifierSystems.INSTITUTION_OBSERVATION_SUB_IDENTIFIER)
        .setValue(obx.getObservationSubId().getValue());

    if (!obx.getReferencesRange().isEmpty()) {
      ReferenceRange referenceRange = new ReferenceRange()
          .setText(obx.getReferencesRange().getValue());
      observation.setReferenceRange(Arrays.asList(referenceRange));
    }

    if (ntes != null && !ntes.isEmpty()) {
      StringJoiner comments = new StringJoiner("\n", "", "\n");
      for (NTE nte : ntes) {
        for (FT ft : nte.getComment()) {
          comments.add(ft.encode());
        }
      }

      observation.setComments(comments.toString());
    }

    return observation;
  }

  /**
   * Casts segments to OBX and NTE, then transforms them to an observation.
   *
   * @param obxSegment
   *     Segment to convert to OBX
   * @param nteSegments
   *     Segments to convert to List<NTE>
   * @return observation
   * @throws HL7Exception if HL7 message is malformed
   */
  private Observation toObservation(
      Segment obxSegment, List<Segment> nteSegments) throws HL7Exception {

    List<NTE> ntes = new ArrayList<>();
    for (Segment segment : nteSegments) {
      ntes.add((NTE) segment);
    }

    return toObservation((OBX) obxSegment, ntes);
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
   * Parses OBX and NTE segments from HL7 message.
   *
   * @param terser
   *     Like XPath for HL7, extracts segments from parsed HL7 specified by a path
   * @param obxRootPath
   *     Terser path to find OBX, varies between message types
   * @param nteRootPath
   *     Terser path to find NTE, varies between message types
   * @return list of observations, empty if none found
   * @throws HL7Exception if HL7 message is malformed
   */
  protected List<Observation> toObservations(
      Terser terser, String obxRootPath, String nteRootPath) throws HL7Exception {

    List<Observation> observations = new ArrayList<>();

    String obxSubscript = "";
    String currentObxPath = obxRootPath.replace(SUBSCRIPT_PLACEHOLDER, obxSubscript);

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

      observations.add(toObservation(obx, notes));

      obxSubscript = incrementSubscript(obxSubscript);
      currentObxPath = obxRootPath.replace(SUBSCRIPT_PLACEHOLDER, obxSubscript);
    }

    return observations;
  }

  protected LocalDate toLocalDate(TSComponentOne fromDate) throws HL7Exception {
    return LocalDate.of(fromDate.getYear(), fromDate.getMonth(), fromDate.getDay());
  }

  protected MaritalStatusCodesEnum toMaritalStatus(String code) {
    MaritalStatusCodesEnum maritalStatus = MaritalStatusCodesEnum.UNK.forCode(code);
    return MoreObjects.firstNonNull(maritalStatus, MaritalStatusCodesEnum.UNK);
  }

  protected RaceEnum toRace(String code) {
    if (code == null) {
      return RaceEnum.UNKNOWN;
    }

    RaceEnum race = RaceMap.raceMap.get(code.toLowerCase());
    return MoreObjects.firstNonNull(race, RaceEnum.UNKNOWN);
  }

  protected CodeableConceptDt toLanguage(String code) {
    LanguageCode languageCode = LanguageCode.getByCodeIgnoreCase(code);
    languageCode = MoreObjects.firstNonNull(languageCode, LanguageCode.undefined);

    CodeableConceptDt codeableConcept = new CodeableConceptDt();
    codeableConcept.addCoding()
        .setCode(languageCode.name())
        .setDisplay(languageCode.getName());
    return codeableConcept;
  }
}
