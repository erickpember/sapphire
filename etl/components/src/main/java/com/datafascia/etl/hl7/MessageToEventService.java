// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.Parser;
import com.datafascia.common.inject.Injectors;
import com.datafascia.domain.event.Event;
import com.google.inject.Injector;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;

/**
 * Transforms HL7 message to events.
 */
@Slf4j
public class MessageToEventService {

  private static final URI UNKNOWN = URI.create("UNKNOWN");

  @Inject
  private Parser parser;

  private final Map<Class<? extends Message>, MessageToEventTransformer>
      messageTypeToTransformerMap = new HashMap<>();

  /**
   * Constructor
   *
   * @param injector
   *     Guice injector
   */
  @Inject
  public MessageToEventService(Injector injector) {
    for (MessageToEventTransformer transformer :
        Injectors.loadService(MessageToEventTransformer.class, injector)) {
      messageTypeToTransformerMap.put(transformer.getApplicableMessageType(), transformer);
      log.debug(
          "loaded transformer of {}",
          new Object[] { transformer.getApplicableMessageType().getName() });
    }
  }

  private Message parseHL7(byte[] bytes) {
    String hl7 = new String(bytes, StandardCharsets.UTF_8);
    try {
      return parser.parse(hl7);
    } catch (HL7Exception e) {
      throw new IllegalStateException("Cannot parse HL7 " + hl7, e);
    }
  }

  /**
   * Transforms HL7 message to events.
   *
   * @param bytes
   *     to transform
   * @return events
   */
  public List<Event> toEvents(byte[] bytes) {
    Message message = parseHL7(bytes);

    MessageToEventTransformer transformer = messageTypeToTransformerMap.get(message.getClass());
    if (transformer == null) {
      log.debug(
          "Do not know how to transform from message type {}",
          new Object[] { message.getClass().getName() });
      return Collections.emptyList();
    }

    List<Event> events = transformer.transform(UNKNOWN, UNKNOWN, message);
    log.debug("Transformed to events {}", new Object[] { events });
    return events;
  }
}
