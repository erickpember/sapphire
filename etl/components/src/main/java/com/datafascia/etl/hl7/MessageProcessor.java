// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.hl7v2.model.Message;
import java.util.function.Consumer;

/**
 * Processes specific HL7 message type.
 */
public interface MessageProcessor extends Consumer<Message> {

  /**
   * Gets type of message this processor accepts.
   *
   * @return message type
   */
  Class<? extends Message> getAcceptableMessageType();
}
