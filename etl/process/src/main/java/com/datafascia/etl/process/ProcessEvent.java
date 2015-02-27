// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.process;

import com.datafascia.domain.event.Event;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

/**
 * Processes event.
 */
@Slf4j
public class ProcessEvent extends BaseFilter {
  private static final long serialVersionUID = 1L;

  public static final String ID = ProcessEvent.class.getSimpleName();

  @Inject
  private transient AdmitPatient admitPatient;

  @Override
  public boolean isKeep(TridentTuple tuple) {
    Event event = (Event) tuple.getValueByField(F.EVENT);

    switch (event.getType()) {
      case PATIENT_ADMIT:
        admitPatient.accept(event);
        break;
      default:
        log.debug("Ignored event type [{}]", event.getType());
    }
    return true;
  }
}
