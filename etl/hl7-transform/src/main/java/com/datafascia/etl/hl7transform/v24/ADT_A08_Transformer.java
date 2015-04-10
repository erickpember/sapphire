// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.domain.event.AddObservationsData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import com.datafascia.domain.event.ObservationType;
import com.google.common.base.Strings;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Extracts OBX data from an A08 message.
 * Further parsing of elements outside the OBX is not yet done.
 *
 * Gotcha: There is no HAPI model for v24.message.ADT_A08, apparently HAPI handles
 * A08 messages as a subtype of A01.
 */
@Slf4j
public class ADT_A08_Transformer extends BaseTransformer {

  private static final String OBX_ROOT_PATH = "/OBX" + SUBSCRIPT_PLACEHOLDER;

  @Override
  public Class<? extends Message> getApplicableMessageType() {
    return ADT_A01.class;
  }

  @Override
  public List<Event> transform(URI institutionId, URI facilityId, Message input) {
    ADT_A01 message = (ADT_A01) input;

    List<Event> outputEvents = new ArrayList<>();
    try {
      Terser terser = new Terser(input);

      // See if OBX exists. Message.getObx() and other Message methods don't work for some reason.
      if (!Strings.isNullOrEmpty(terser.get(OBX_ROOT_PATH.replace(SUBSCRIPT_PLACEHOLDER, "")
          + "-1"))) {

        AddObservationsData addObservationsData = toAddObservationsData(
            message.getPID(),
            message.getPV1(),
            OBX_ROOT_PATH,
            "",
            terser,
            ObservationType.A08);

        outputEvents.add(Event.builder()
            .institutionId(institutionId)
            .facilityId(facilityId)
            .type(EventType.OBSERVATIONS_ADD)
            .data(addObservationsData)
            .build());
      }

      return outputEvents;
    } catch (HL7Exception e) {
      log.debug("HL7 transformer failed to transform input: {}", input);
      throw new IllegalStateException("Failed to transform HL7 message", e);
    }
  }
}
