// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A09;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles discharge patient message.
 */
@Slf4j
public class ADT_A09_Transformer extends AdmitPatientTransformer {

  @Override
  public Class<? extends Message> getApplicableMessageType() {
    return ADT_A09.class;
  }

  @Override
  public List<Event> transform(URI institutionId, URI facilityId, Message input) {
    ADT_A09 message = (ADT_A09) input;

    List<Event> outputEvents = new ArrayList<>();
    try {
      AdmitPatientData admitPatientData = toAdmitPatientData(
          message.getMSH(), message.getPID(), message.getPV1());

      String triggerEvent = message.getMSH().getMessageType().getTriggerEvent().getValue();
      EventType eventType = triggerEvent.equals("A12")
          ? EventType.PATIENT_ADMIT : EventType.PATIENT_DISCHARGE;

      outputEvents.add(Event.builder()
          .institutionId(institutionId)
          .facilityId(facilityId)
          .type(eventType)
          .data(admitPatientData)
          .build());

      return outputEvents;
    } catch (HL7Exception e) {
      log.debug("HL7 transformer failed to transform input: {}", input);
      throw new IllegalStateException("Failed to transform HL7 message", e);
    }
  }
}
