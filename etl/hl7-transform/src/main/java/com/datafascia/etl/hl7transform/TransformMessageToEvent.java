// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import ca.uhn.hl7v2.model.Message;
import com.datafascia.domain.event.Event;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import lombok.extern.slf4j.Slf4j;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

/**
 * Transforms message to normalized event.
 */
@Slf4j
public class TransformMessageToEvent extends BaseFunction {
  private static final long serialVersionUID = 1L;

  public static final String ID = TransformMessageToEvent.class.getSimpleName();

  public static final Fields OUTPUT_FIELDS = new Fields(F.EVENT);

  private transient Map<Class<? extends Message>, MessageToEventTransformer>
      messageTypeToTransformerMap;

  @Override
  public void prepare(Map config, TridentOperationContext context) {
    super.prepare(config, context);

    messageTypeToTransformerMap = new HashMap<>();
    ServiceLoader<MessageToEventTransformer> serviceLoader =
        ServiceLoader.load(MessageToEventTransformer.class);
    for (MessageToEventTransformer transformer : serviceLoader) {
      messageTypeToTransformerMap.put(transformer.getApplicableMessageType(), transformer);
    }
  }

  @Override
  public void execute(TridentTuple tuple, TridentCollector collector) {
    URI institutionId = (URI) tuple.getValueByField(F.INSTITUTION_ID);
    URI facilityId = (URI) tuple.getValueByField(F.FACILITY_ID);
    Message message = (Message) tuple.getValueByField(F.MESSAGE);

    MessageToEventTransformer transformer = messageTypeToTransformerMap.get(message.getClass());
    if (transformer == null) {
      log.debug("Do not know how to transform message type {}", message.getClass());
      return;
    }

    try {
      List<Event> events = transformer.transform(institutionId, facilityId, message);
      for (Event event : events) {
        collector.emit(new Values(event));
        log.debug("Transform transformed Message to Event. institutionId:{}, facilityId:{},"
            + " type:{}, data:{}", event.getInstitutionId(), event.getFacilityId(), event.getType(),
            event.getData());
      }
    } catch (IllegalStateException e) {
      log.error("Cannot transform message type " + message.getClass(), e);
    }
  }
}
