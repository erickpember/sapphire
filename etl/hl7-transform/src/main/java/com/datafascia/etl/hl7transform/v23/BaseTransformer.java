// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v23;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.model.Varies;
import ca.uhn.hl7v2.model.v23.datatype.CE;
import ca.uhn.hl7v2.model.v23.datatype.FT;
import ca.uhn.hl7v2.model.v23.datatype.ID;
import ca.uhn.hl7v2.model.v23.datatype.TSComponentOne;
import ca.uhn.hl7v2.model.v23.segment.NTE;
import ca.uhn.hl7v2.model.v23.segment.OBX;
import com.datafascia.domain.event.ObservationData;
import com.datafascia.domain.event.ObservationType;
import com.datafascia.etl.hl7transform.MessageToEventTransformer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Implements common methods for transformers.
 */
public abstract class BaseTransformer extends MessageToEventTransformer {

  private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");
  public static final String V23 = "2.3";

  @Override
  protected ObservationData segmentsToObservationData(String version, Segment obx,
      List<Segment> segmentNotes, ObservationType observationType) throws HL7Exception {
    ArrayList<NTE> ntes = new ArrayList<>();
    for (Segment seg : segmentNotes) {
      ntes.add((NTE) seg);
    }

    return toObservationData(version, (OBX) obx, ntes, observationType);
  }

  /**
   * Takes an OBX and it's associated NTEs and converts it into an Avro-friendly POJO.
   * @param version The version of HL7.
   * @param obx The OBX segment.
   * @param ntes The NTE segments.
   * @param observationType The type of message it came from.
   * @return Avro-friendly Pojo.
   * @throws HL7Exception
   */
  protected ObservationData toObservationData(String version, OBX obx, List<NTE> ntes,
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
    for (ID id : obx.getAbnormalFlags()) {
      abnormalFlags.add(id.encode());
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
            obx.getDateLastObsNormalValues().getTimeOfAnEvent().encode())
        .userDefinedAccessChecks(obx.getUserDefinedAccessChecks().encode())
        .observationDateAndTime(obx.getDateTimeOfTheObservation().getTimeOfAnEvent().encode())
        .responsibleObserver(obx.getResponsibleObserver().encode())
        .resultStatus(obx.getObservResultStatus().encode())
        .probability(obx.getProbability().encode())
        .producersId(obx.getProducerSID().encode())
        .valueUnits(obx.getUnits().encode())
        .valueType(obx.getValueType().encode())
        .subId(obx.getObservationSubID().encode())
        .value(observationValue)
        .id(obx.getObservationIdentifier().encode())
        .referenceRange(obx.getReferencesRange().encode())
        .observationType(observationType)
        .hl7Version(version).build();
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
