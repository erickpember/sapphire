// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
