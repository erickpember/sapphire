// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.fhir.model.api.IDatatype;
import ca.uhn.fhir.model.dstu2.composite.CodeableConceptDt;
import ca.uhn.fhir.model.dstu2.composite.QuantityDt;
import ca.uhn.fhir.model.dstu2.resource.Observation;
import ca.uhn.fhir.model.dstu2.resource.Observation.ReferenceRange;
import ca.uhn.fhir.model.dstu2.valueset.ObservationStatusEnum;
import ca.uhn.fhir.model.primitive.DateTimeDt;
import ca.uhn.fhir.model.primitive.DecimalDt;
import ca.uhn.fhir.model.primitive.StringDt;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.v24.datatype.CE;
import ca.uhn.hl7v2.model.v24.datatype.FT;
import ca.uhn.hl7v2.model.v24.segment.NTE;
import ca.uhn.hl7v2.model.v24.segment.OBX;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.domain.fhir.IdentifierSystems;
import com.google.common.base.Strings;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;

/**
 * Transforms HL7 OBX segments to FHIR Observations.
 */
public class ObservationsBuilder {

  private String obxPathPattern;
  private String ntePathPattern;
  private Terser terser;

  /**
   * Constructor
   *
   * @param message
   *     HL7 message
   * @param obxPathPattern
   *     pattern to generate path to each OBX segment
   * @param ntePathPattern
   *     pattern to generate path to each NTE segment
   */
  public ObservationsBuilder(Message message, String obxPathPattern, String ntePathPattern) {
    this.obxPathPattern = obxPathPattern;
    this.ntePathPattern = ntePathPattern;
    terser = new Terser(message);
  }

  private String formatObxPath(int obxIndex) {
    return String.format(obxPathPattern, obxIndex);
  }

  private String formatNtePath(int obxIndex, int nteIndex) {
    return String.format(ntePathPattern, obxIndex, nteIndex);
  }

  /**
   * @return true if HL7 message contains observations
   * @throws HL7Exception if HL7 message is malformed
   */
  public boolean hasObservations() throws HL7Exception {
    String obxPath = formatObxPath(0);
    return !Strings.isNullOrEmpty(terser.get(obxPath + "-1"));
  }

  private List<Segment> getNteSegments(int obxIndex) throws HL7Exception {
    List<Segment> notes = new ArrayList<>();
    if (!Strings.isNullOrEmpty(ntePathPattern)) {
      for (int nteIndex = 0; true; ++nteIndex) {
        String ntePath = formatNtePath(obxIndex, nteIndex);
        if (Strings.isNullOrEmpty(terser.get(ntePath + "-1"))) {
          break;
        }

        notes.add(terser.getSegment(ntePath));
      }
    }

    return notes;
  }

  private static CodeableConceptDt toCodeableConcept(CE ce) throws HL7Exception {
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
  private Observation toObservation(OBX obx, List<NTE> ntes) throws HL7Exception {
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
   * Parses OBX and NTE segments from HL7 message.
   *
   * @return list of observations, empty if none found
   * @throws HL7Exception if HL7 message is malformed
   */
  public List<Observation> toObservations() throws HL7Exception {
    List<Observation> observations = new ArrayList<>();
    for (int obxIndex = 0; true; ++obxIndex) {
      String obxPath = formatObxPath(obxIndex);
      if (Strings.isNullOrEmpty(terser.get(obxPath + "-1"))) {
        break;
      }

      Segment obx = terser.getSegment(obxPath);
      List<Segment> notes = getNteSegments(obxIndex);
      observations.add(toObservation(obx, notes));
    }

    return observations;
  }
}
