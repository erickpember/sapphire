// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.kafka;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Configuration parameters for Kafka
 */
@Slf4j @Getter @Setter @JsonAutoDetect @NoArgsConstructor
public class KafkaConfig {
  /** Kafka ZooKeeper list */
  private String zooKeepers;
  /** Kafka group identifier */
  private String groupId;
}
