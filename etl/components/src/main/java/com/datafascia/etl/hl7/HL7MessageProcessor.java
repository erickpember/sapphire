// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import java.util.function.Consumer;
import javax.inject.Inject;

/**
 * Updates application state in response to HL7 message.
 */
public class HL7MessageProcessor implements Consumer<String> {

  @Inject
  private Parser parser;

  @Inject
  private MessageRouter messageRouter;

  @Override
  public void accept(String hl7) {
    try {
      Message message = parser.parse(hl7);
      messageRouter.accept(message);
    } catch (HL7Exception e) {
      throw new IllegalStateException("Cannot parse HL7 " + hl7, e);
    }
  }
}
