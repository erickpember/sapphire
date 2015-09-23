// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.ADT_A03;
import com.datafascia.etl.hl7.MessageProcessor;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.MetaInfServices;

/**
 * Processes discharge patient message.
 */
@MetaInfServices(MessageProcessor.class)
@Slf4j
public class ADT_A03_Processor extends AdmitDischargeProcessor {

  @Override
  public Class<? extends Message> getAcceptableMessageType() {
    return ADT_A03.class;
  }

  @Override
  public void accept(Message input) {
    ADT_A03 message = (ADT_A03) input;

    try {
      addObservations(message, message.getPID(), message.getPV1());
      dischargePatient(message.getMSH(), message.getPV1());
    } catch (HL7Exception e) {
      log.error("Failed to process message {}", message);
      throw new IllegalStateException("Failed to process message", e);
    }
  }
}
