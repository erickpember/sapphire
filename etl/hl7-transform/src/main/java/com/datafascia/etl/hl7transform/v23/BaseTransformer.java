// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v23;

import ca.uhn.hl7v2.HL7Exception;
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
public abstract class BaseTransformer implements MessageToEventTransformer {

  private static final ZoneId TIME_ZONE = ZoneId.of("America/Los_Angeles");

  /**
   * Takes an OBX and it's associated NTE and converts it into an Avro-friendly POJO.
   * @param obx The OBX segment.
   * @param nte The NTE segment.
   * @param observationType The type of message it came from.
   * @return Avro-friendly Pojo.
   * @throws HL7Exception
   */
  protected ObservationData toObservationData(OBX obx, NTE nte, ObservationType observationType)
      throws HL7Exception {
    List<String> observationMethod = new ArrayList<>();
    for (CE ce : obx.getObservationMethod()) {
      observationMethod.add(ce.getText().encode());
    }

    List<String> comments = new ArrayList<>();
    for (FT ft : nte.getComment()) {
      comments.add(ft.encode());
    }

    List<String> abnormalFlags = new ArrayList<>();
    for (ID id : obx.getAbnormalFlags()) {
      abnormalFlags.add(id.encode());
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
        .value(obx.getObservationValue()[0].encode())
        .id(obx.getSetIDOBX().encode())
        .referenceRange(obx.getReferencesRange().encode())
        .observationType(observationType).build();
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
