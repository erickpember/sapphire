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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import java.util.Properties;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Configuration parameters for Kafka consumers.
 */
@Slf4j @Getter @Setter @JsonAutoDetect @NoArgsConstructor
public class KafkaConfig {

  /** Property name for Zookeepers for consumerConfig */
  public static final String ZOOKEEPERS = "zookeeper.connect";
  /** Property name for Consumer groupid */
  public static final String GROUP = "group.id";
  /** Property name for Consumer auto offset */
  public static final String AUTOOFFSETRESET = "auto.offset.reset";
  /** Property name for Consumer timeout in milliseconds */
  public static final String TIMEOUT = "consumer.timeout.ms";

  /** Zookeepers for consumerConfig, ex: 0.0.0.0:2181 */
  private String zookeeperConnect;
  /** Consumer groupid, default: group */
  private String groupId = "group";
  /** Consumer auto offset, ex: smallest */
  private String autoOffsetReset;
  /** Consumer timeout in milliseconds, -1 for no timeout */
  private String consumerTimeoutMs = "-1";

  /**
   * Builds the configuration used to build a Kafka Consumer.
   *
   * Kafka wants properties with dots in them. DropWizard wants to map properties to java variables
   * by the same name, which have no dots, in accordance with Java naming conventions. This method
   * maps one naming convention to the other.
   *
   * @return configuration used to build a Kafka Consumer
   */
  public Properties buildConsumerConfig() {
    Properties props = new Properties();
    props.setProperty(ZOOKEEPERS, zookeeperConnect);
    props.setProperty(GROUP, groupId);
    props.setProperty(AUTOOFFSETRESET, autoOffsetReset);
    props.setProperty(TIMEOUT, consumerTimeoutMs);
    return props;
  }
}
