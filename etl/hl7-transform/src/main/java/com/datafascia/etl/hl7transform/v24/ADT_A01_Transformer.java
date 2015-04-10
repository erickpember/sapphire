// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A01;
import ca.uhn.hl7v2.util.Terser;
import com.datafascia.domain.event.AdmitPatientData;
import com.datafascia.domain.event.Event;
import com.datafascia.domain.event.EventType;
import com.datafascia.domain.event.ObservationListData;
import com.datafascia.domain.event.ObservationType;
import com.google.common.base.Strings;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles admit patient message.
 */
@Slf4j
public class ADT_A01_Transformer extends BaseTransformer {

  private static final String OBX_ROOT_PATH = "/OBX" + SUBSCRIPT_PLACEHOLDER;

  @Override
  public Class<? extends Message> getApplicableMessageType() {
    return ADT_A01.class;
  }

  @Override
  public List<Event> transform(URI institutionId, URI facilityId, Message input) {
    List<Event> outputEvents = new ArrayList<>();
    ADT_A01 message = (ADT_A01) input;

    try {
      AdmitPatientData admitPatientData = toAdmitData(message.getPID(), message.getPV1());

      outputEvents.add(Event.builder()
          .institutionId(institutionId)
          .facilityId(facilityId)
          .type(EventType.PATIENT_ADMIT)
          .data(admitPatientData)
          .build());

      Terser terser = new Terser(input);

      // See if OBX exists. Message.getObx() and other Message methods don't work for some reason.
      if (!Strings.isNullOrEmpty(terser.get(OBX_ROOT_PATH.replace(SUBSCRIPT_PLACEHOLDER, "")
          + "-1"))) {
        ObservationListData observationList = extractObx(OBX_ROOT_PATH, "", terser,
            ObservationType.A01);

        outputEvents.add(Event.builder()
            .institutionId(institutionId)
            .facilityId(facilityId)
            .type(EventType.OBSERVATION)
            .data(observationList)
            .build());
      }

      return outputEvents;
    } catch (HL7Exception e) {
      log.debug("HL7 transformer failed to transform input:{}", input);
      throw new IllegalStateException("Transform failed to build PatientData from HL7 message.", e);
    }
  }
}
