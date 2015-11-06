// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A09;
import com.datafascia.etl.hl7.MessageProcessor;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.MetaInfServices;

/**
 * Processes discharge patient message.
 */
@MetaInfServices(MessageProcessor.class)
@Slf4j
public class ADT_A09_Processor extends AdmitDischargeProcessor {

  @Override
  public Class<? extends Message> getAcceptableMessageType() {
    return ADT_A09.class;
  }

  @Override
  public void accept(Message input) {
    ADT_A09 message = (ADT_A09) input;

    try {
      String triggerEvent = message.getMSH().getMessageType().getTriggerEvent().getValue();
      if (triggerEvent.equals("A12")) {
        admitPatient(
            message.getMSH(),
            message.getPID(),
            message.getPV1(),
            Collections.emptyList(),
            Collections.emptyList());
        addObservations(message, message.getPID(), message.getPV1());
      } else {
        addObservations(message, message.getPID(), message.getPV1());
        dischargePatient(
            message.getMSH(), message.getPV1(), Collections.emptyList(), Collections.emptyList());
      }
    } catch (HL7Exception e) {
      log.error("Failed to process message {}", message);
      throw new IllegalStateException("Failed to process message", e);
    }
  }
}
