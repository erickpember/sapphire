// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A02;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import com.datafascia.domain.event.ObservationType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Transforms transfer patient message.
 */
@Slf4j
public class ADT_A02_Transformer extends AdmitPatientTransformer {

  @Override
  public Class<? extends Message> getApplicableMessageType() {
    return ADT_A02.class;
  }

  @Override
  public List<Event> transform(URI institutionId, URI facilityId, Message input) {
    ADT_A02 message = (ADT_A02) input;

    List<Event> outputEvents = new ArrayList<>();
    try {
      AdmitPatientData admitPatientData = toAdmitPatientData(message.getPID(), message.getPV1());

      outputEvents.add(Event.builder()
          .institutionId(institutionId)
          .facilityId(facilityId)
          .type(EventType.PATIENT_ADMIT)
          .data(admitPatientData)
          .build());

      toAddObservationsEvent(
          input, message.getPID(), message.getPV1(), institutionId, facilityId, ObservationType.A02)
          .ifPresent(event -> outputEvents.add(event));

      return outputEvents;
    } catch (HL7Exception e) {
      log.error("Failed to transform HL7 message {}", input);
      throw new IllegalStateException("Failed to transform HL7 message", e);
    }
  }
}
