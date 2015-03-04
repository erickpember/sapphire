// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A03;
import com.datafascia.domain.event.AdmitData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles discharge patient message.
 */
@Slf4j
public class ADT_A03_Transformer extends BaseTransformer {

  @Override
  public Class<? extends Message> getApplicableMessageType() {
    return ADT_A03.class;
  }

  @Override
  public Event transform(URI institutionId, URI facilityId, Message input) {
    ADT_A03 message = (ADT_A03) input;
    try {
      AdmitData admitData = toAdmitData(message.getPID(), message.getPV1());

      return Event.builder()
          .institutionId(institutionId)
          .facilityId(facilityId)
          .type(EventType.PATIENT_DISCHARGE)
          .data(admitData)
          .build();
    } catch (HL7Exception e) {
      log.debug("HL7 transformer failed to transform input:{}", input);
      throw new IllegalStateException("Transform failed to build PatientData from HL7 message.", e);
    }
  }
}
