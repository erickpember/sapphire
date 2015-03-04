// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import com.datafascia.common.avro.Deserializer;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.configuration.ConfigurationNode;
import com.datafascia.common.configuration.Configure;
import com.datafascia.domain.model.IngestMessage;
import java.util.Map;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import storm.trident.operation.BaseFunction;
import storm.trident.operation.TridentCollector;
import storm.trident.operation.TridentOperationContext;
import storm.trident.tuple.TridentTuple;

/**
 * Deserializes message read from queue.
 */
@ConfigurationNode("hl7MessageQueue") @Slf4j
public class DeserializeMessage extends BaseFunction {
  private static final long serialVersionUID = 1L;

  public static final String ID = DeserializeMessage.class.getSimpleName();

  public static final Fields OUTPUT_FIELDS = new Fields(F.INGEST_MESSAGE);

  @Configure
  private transient String topic;

  @Inject
  private transient AvroSchemaRegistry schemaRegistry;

  @Inject
  private transient Deserializer deserializer;

  @Override
  public void prepare(Map config, TridentOperationContext context) {
    super.prepare(config, context);

    Schema schema = ReflectData.get().getSchema(IngestMessage.class);
    schemaRegistry.putSchema(topic, schema);
  }

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
    byte[] bytes = tuple.getBinaryByField(F.BYTES);

    IngestMessage message = deserializer.decodeReflect(topic, bytes, IngestMessage.class);

    log.debug("Transform deserialized message:{}", message);

    collector.emit(new Values(message));
  }
}
