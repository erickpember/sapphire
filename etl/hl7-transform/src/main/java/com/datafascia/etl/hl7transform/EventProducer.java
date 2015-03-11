// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.configuration.ConfigurationNode;
import com.datafascia.common.configuration.Configure;
import com.datafascia.common.kafka.SingleTopicProducer;
import com.datafascia.domain.event.Event;
import javax.inject.Inject;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;

/**
 * Sends event to Kafka.
 */
@ConfigurationNode("eventQueue")
public class EventProducer {

  @Configure
  private String kafkaBrokers;

  @Configure
  protected String topic;

  private final AvroSchemaRegistry schemaRegistry;
  private final Serializer serializer;
  private SingleTopicProducer producer;

  /**
   * Constructor
   *
   * @param schemaRegistry
   *     schema registry
   * @param serializer
   *     serializer
   */
  @Inject
  public EventProducer(AvroSchemaRegistry schemaRegistry, Serializer serializer) {
    this.schemaRegistry = schemaRegistry;
    this.serializer = serializer;
  }

  protected SingleTopicProducer createProducer() {
    return new SingleTopicProducer(kafkaBrokers, topic);
  }

  /**
   * Initializes Kafka producer.
   */
  public void initialize() {
    Schema schema = ReflectData.get().getSchema(Event.class);
    schemaRegistry.putSchema(topic, schema);

    producer = createProducer();
  }

  /**
   * Sends event to Kafka.
   *
   * @param event
   *     to send
   */
  public void send(Event event) {
    producer.send(serializer.encodeReflect(topic, event));
  }
}
