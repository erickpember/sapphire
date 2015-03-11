// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.kafka;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.nio.charset.Charset;
import kafka.message.MessageAndMetadata;

/**
 * Internal format for messages pulled from Kafka.
 */
public class KafkaMessage {

  /** A named channel in Kafka to publish to and from which to subscribe. */
  public String topic;

  /** Optional key associated with the message. */
  @JsonInclude(JsonInclude.Include.NON_NULL)
  public String key;

  /** The text of the Kafka message. */
  public String message;

  /** If the topic is divided into partitions, this is what distinguishes those partitions. */
  public int partition;

  /**
   * Position of the consumer's place reading the chronological order of messages in this partition
   * and topic.
   */
  public long offset;

  /**
   * Translate Kafka's message format to create our message.
   *
   * @param message the kafka message
   */
  public KafkaMessage(MessageAndMetadata<byte[], byte[]> message) {
    this.topic = message.topic();
    this.key = message.key() != null ? new String(message.key(), Charset.forName("utf-8")) : null;
    this.message = new String(message.message(), Charset.forName("utf-8"));
    this.partition = message.partition();
    this.offset = message.offset();
  }
}
