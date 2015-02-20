// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.datafascia.domain.model.IngestMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.tuple.TridentTuple;

/**
 * Deserializes message read from queue.
 */
@Slf4j
public class DeserializeMessage extends BaseFunction {
  private static final long serialVersionUID = 1L;

  public static final String ID = DeserializeMessage.class.getSimpleName();

  public static final Fields OUTPUT_FIELDS = new Fields(F.INGEST_MESSAGE);

  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();

  /**
   * Deserializes message.
   *
   * @param tuple
   *     tuple containing the fields:
   *     <dl>
   *     <dt>bytes<dd>encoded message from queue
   *     </dl>
   * @param collector
   *     used to emit the resulting tuple
   */
  @Override
  public void execute(TridentTuple tuple, TridentCollector collector) {
    try {
      IngestMessage message = OBJECT_MAPPER.readValue(
          tuple.getBinaryByField(F.BYTES), IngestMessage.class);
      collector.emit(new Values(message));
    } catch (IOException e) {
      throw new IllegalStateException("Error deserializing JSON", e);
    }
  }
}
