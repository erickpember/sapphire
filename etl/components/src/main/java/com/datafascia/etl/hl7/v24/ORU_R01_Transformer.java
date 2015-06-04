// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ORU_R01;
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
 * Extracts OBX data from an ORU message.
 * Further parsing of elements outside the OBX is not yet done.
 */
@Slf4j
public class ORU_R01_Transformer extends BaseTransformer {

  // HAPI paths to retrieve the segments. Differs between some message types.
  private static final String OBX_ROOT_PATH
      = "/PATIENT_RESULT/ORDER_OBSERVATION/OBSERVATION" + SUBSCRIPT_PLACEHOLDER + "/OBX";
  private static final String NTE_ROOT_PATH
      = "/PATIENT_RESULT/ORDER_OBSERVATION/OBSERVATION" + SUBSCRIPT_PLACEHOLDER + "/NTE";

  @Override
  public Class<? extends Message> getApplicableMessageType() {
    return ORU_R01.class;
  }

  @Override
  public List<Event> transform(URI institutionId, URI facilityId, Message input) {
    ORU_R01 message = (ORU_R01) input;

    List<Event> outputEvents = new ArrayList<>();
    try {
      Terser terser = new Terser(input);

      // See if OBX exists. Message.getObx() and other Message methods don't work for some reason.
      if (!Strings.isNullOrEmpty(terser.get(OBX_ROOT_PATH.replace(SUBSCRIPT_PLACEHOLDER, "")
          + "-1"))) {
        AddObservationsData addObservationsData = toAddObservationsData(
            message.getPATIENT_RESULT().getPATIENT().getPID(),
            message.getPATIENT_RESULT().getPATIENT().getVISIT().getPV1(),
            OBX_ROOT_PATH,
            NTE_ROOT_PATH,
            terser,
            ObservationType.ORU);

        outputEvents.add(Event.builder()
            .institutionId(institutionId)
            .facilityId(facilityId)
            .type(EventType.OBSERVATIONS_ADD)
            .data(addObservationsData)
            .build());
      }

      return outputEvents;
    } catch (HL7Exception e) {
      throw new IllegalStateException("Transform failed to extract data from HL7 message", e);
    }
  }
}
