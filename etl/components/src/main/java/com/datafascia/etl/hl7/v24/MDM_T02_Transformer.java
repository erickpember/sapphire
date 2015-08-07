// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.MDM_T02;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.ObservationType;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Extracts OBX data from an MDM message.
 */
@Slf4j
public class MDM_T02_Transformer extends AdmitPatientTransformer {

  @Override
  public Class<? extends Message> getApplicableMessageType() {
    return MDM_T02.class;
  }

  @Override
  public List<Event> transform(URI institutionId, URI facilityId, Message input) {
    MDM_T02 message = (MDM_T02) input;

    List<Event> outputEvents = new ArrayList<>();
    try {
      toAddObservationsEvent(
          input, message.getPID(), message.getPV1(), institutionId, facilityId, ObservationType.MDM)
          .ifPresent(event -> outputEvents.add(event));

      return outputEvents;
    } catch (HL7Exception e) {
      log.error("HL7 transformer failed to transform input {}", input);
      throw new IllegalStateException("Transform failed to extract data from HL7 message", e);
    }
  }
}
