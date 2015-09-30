// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7.v24;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.model.v24.message.MDM_T02;
import ca.uhn.hl7v2.model.v24.segment.TXA;
import com.datafascia.etl.event.AddFlag;
import com.datafascia.etl.hl7.MessageProcessor;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.kohsuke.MetaInfServices;

/**
 * Extracts OBX data from an MDM message.
 */
@MetaInfServices(MessageProcessor.class)
@Slf4j
public class MDM_T02_Processor extends AdmitDischargeProcessor {

  @Inject
  private AddFlag addFlag;

  @Override
  public Class<? extends Message> getAcceptableMessageType() {
    return MDM_T02.class;
  }

  private void processDocumentNotification(MDM_T02 message) throws HL7Exception {
    TXA txa = message.getTXA();
    addFlag.accept(
        txa.getDocumentType().getValue(),
        toDateTime(txa.getActivityDateTime()),
        getPatientIdentifier(message.getPID()));
  }

  @Override
  public void accept(Message input) {
    MDM_T02 message = (MDM_T02) input;

    try {
      processDocumentNotification(message);
      addObservations(message, message.getPID(), message.getPV1());
    } catch (HL7Exception e) {
      log.error("Failed to process message {}", message);
      throw new IllegalStateException("Failed to process message", e);
    }
  }
}
