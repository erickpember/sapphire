// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import ca.uhn.hl7v2.model.Message;
import storm.trident.operation.BaseFilter;
import storm.trident.tuple.TridentTuple;

/**
 * Handles observation message.
 */
public class HandleObservationMessage extends BaseFilter {
  private static final long serialVersionUID = 1L;

  public static final String ID = HandleObservationMessage.class.getSimpleName();

  @Override
  public boolean isKeep(TridentTuple tuple) {
    Message message = (Message) tuple.getValueByField(F.MESSAGE);

    return true;
  }
}
