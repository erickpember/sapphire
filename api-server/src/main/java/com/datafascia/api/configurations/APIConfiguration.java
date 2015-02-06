// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.configurations;

import com.datafascia.accumulo.AccumuloConfiguration;
import com.datafascia.kafka.KafkaConfig;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.dropwizard.Configuration;
import javax.validation.constraints.NotNull;
import lombok.Data;

/**
 * Configuration parameters for dataFascia API server
 */
@Data
public class APIConfiguration extends Configuration {

  @JsonProperty("accumulo") @NotNull
  private AccumuloConfiguration accumuloConfiguration;

  @NotNull
  private KafkaConfig kafkaConfig;
}
