// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A03;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import com.datafascia.domain.event.PatientData;
import java.net.URI;

/**
 * Handles discharge patient message.
 */
public class ADT_A03_Transformer extends BaseTransformer {

  @Override
  public Class<? extends Message> getApplicableMessageType() {
    return ADT_A03.class;
  }

  @Override
  public Event transform(URI institutionId, URI facilityId, Message input) {
    ADT_A03 message = (ADT_A03) input;
    try {
      PatientData patientData = toPatientData(message.getPID());

      return Event.builder()
          .institutionId(institutionId)
          .facilityId(facilityId)
          .type(EventType.DISCHARGE_PATIENT)
          .data(patientData)
          .build();
    } catch (HL7Exception e) {
      throw new IllegalStateException("transform", e);
    }
  }
}
