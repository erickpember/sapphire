// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.Segment;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.ObservationData;
import com.datafascia.domain.event.ObservationListData;
import com.datafascia.domain.event.ObservationType;
import com.google.common.base.Strings;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

/**
 * Transforms message to event.
 */
public abstract class MessageToEventTransformer {

  protected static final String SUBSCRIPT_PLACEHOLDER = "REPLACEME";

  /**
   * Gets type of message this transformer accepts.
   *
   * @return message type
   */
  protected abstract Class<? extends Message> getApplicableMessageType();

  /**
   * Transforms message to one or more events
   *
   * @param institutionId
   *     institution ID
   * @param facilityId
   *     facility ID
   * @param message
   *     to transform
   * @return a list of one or more events
   */
  protected abstract List<Event> transform(URI institutionId, URI facilityId, Message message);

  /**
   * This method must convert segments to appropriate versions of NTE and OBX in order to call
   * and wrap toObservationData.
   *
   * @param version Hl7 version.
   * @param obx Segment to convert to OBX
   * @param segmentNotes Segment to convert to List<NTE>
   * @param observationType Type of message from which the aforementioned segments were extracted.
   * @return EventData representation of OBX segment.
   * @throws HL7Exception Parsing error encountered in toObservationData.
   */
  protected abstract ObservationData segmentsToObservationData(String version, Segment obx,
      List<Segment> segmentNotes, ObservationType observationType) throws HL7Exception;

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
   * @param obxRootPath Terser path to find OBX, varies between message types.
   * @param nteRootPath Terser path to find NTE, varies between message types.
   * @param terser Like XPath for HL7, extracts segments from parsed HL7 using a path.
   * @param observationType The subtype of the wrapping message, such as AO1.
   * @param version Version of HL7 to parse.
   * @return EventData subclass containing a list of Observations stored in our internal format.
   * @throws ca.uhn.hl7v2.HL7Exception Failure to parse HL7 with terser.
   */
  protected ObservationListData extractObx(String obxRootPath, String nteRootPath, Terser terser,
      ObservationType observationType, String version)
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
      observations.add(segmentsToObservationData(version, obx, notes, observationType));

      obxSubscript = incrementSubscript(obxSubscript);
      currentObxPath = obxRootPath.replace(SUBSCRIPT_PLACEHOLDER, obxSubscript);
    }
    return ObservationListData.builder()
        .observations(observations)
        .build();
  }
}
