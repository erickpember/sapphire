// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import com.datafascia.common.inject.Injectors;
import com.google.inject.Injector;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Routes HL7 message to a processor which understands that specific message type.
 */
@Slf4j
public class MessageRouter implements Consumer<Message> {

  @Inject
  private Parser parser;

  private final Map<Class<? extends Message>, MessageProcessor> messageTypeToProcessorMap =
      new HashMap<>();

  /**
   * Constructor
   *
   * @param injector
   *     Guice injector
   */
  @Inject
  public MessageRouter(Injector injector) {
    for (MessageProcessor processor :
        Injectors.loadService(MessageProcessor.class, injector)) {
      messageTypeToProcessorMap.put(processor.getAcceptableMessageType(), processor);
      log.debug(
          "loaded processor of {}", processor.getAcceptableMessageType().getName());
    }
  }

  @Override
  public void accept(Message message) {
    MessageProcessor processor = messageTypeToProcessorMap.get(message.getClass());
    if (processor != null) {
      processor.accept(message);
    } else {
      log.debug(
          "Do not know how to process message type {}", message.getClass().getName());
    }
  }
}
