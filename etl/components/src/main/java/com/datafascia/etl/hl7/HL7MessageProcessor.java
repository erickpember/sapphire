// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import com.datafascia.domain.event.Event;
import com.datafascia.etl.event.AddObservations;
import com.datafascia.etl.event.AdmitPatient;
import com.datafascia.etl.event.DischargePatient;
import java.util.function.Consumer;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Updates application state in response to HL7 message.
 */
@Slf4j
public class HL7MessageProcessor implements Consumer<byte[]> {

  @Inject
  private MessageToEventService messageToEventService;

  @Inject
  private AdmitPatient admitPatient;

  @Inject
  private DischargePatient dischargePatient;

  @Inject
  private AddObservations addObservations;

  private void processEvent(Event event) {
    switch (event.getType()) {
      case PATIENT_ADMIT:
        admitPatient.accept(event);
        break;
      case PATIENT_DISCHARGE:
        dischargePatient.accept(event);
        break;
      case OBSERVATIONS_ADD:
        addObservations.accept(event);
        break;
      default:
        log.debug("Ignored event type {}", event.getType());
    }
  }

  @Override
  public void accept(byte[] message) {
    messageToEventService.toEvents(message)
        .forEach(this::processEvent);
  }
}
