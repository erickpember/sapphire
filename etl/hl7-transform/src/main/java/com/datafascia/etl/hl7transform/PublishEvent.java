// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import com.datafascia.domain.event.Event;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import storm.trident.operation.BaseFilter;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

/**
 * Sends event to Kafka.
 */
@Slf4j
public class PublishEvent extends BaseFilter {
  private static final long serialVersionUID = 1L;

  public static final String ID = PublishEvent.class.getSimpleName();

  @Inject
  private transient EventProducer producer;

  @Override
  public void prepare(Map config, TridentOperationContext context) {
    super.prepare(config, context);

    producer.initialize();
  }

  @Override
  public boolean isKeep(TridentTuple tuple) {
    Event event = (Event) tuple.getValueByField(F.EVENT);
    producer.send(event);

    log.debug("Transform sent Event to Kafka.");
    return true;
  }
}
