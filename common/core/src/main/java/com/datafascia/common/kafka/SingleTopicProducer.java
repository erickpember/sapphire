// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.kafka;

import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

/**
 * Sends messages to one configured topic.
 */
public class SingleTopicProducer {

  private Producer<byte[], byte[]> producer;
  private String topic;

  /**
   * Constructor
   *
   * @param kafkaBrokers
   *     comma separated list of Kafka brokers
   * @param topic
   *     topic name
   */
  public SingleTopicProducer(String kafkaBrokers, String topic) {
    Properties properties = new Properties();
    properties.put("metadata.broker.list", kafkaBrokers);
    properties.put("request.required.acks", "1");
    producer = new Producer<>(new ProducerConfig(properties));

    this.topic = topic;
  }

  /**
   * Sends message.
   *
   * @param message
   *     to send
   */
  public void send(byte[] message) {
    producer.send(new KeyedMessage<>(topic, message));
  }
}
