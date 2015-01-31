// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.configurations;

import com.datafascia.accumulo.AccumuloConfig;
import com.datafascia.kafka.KafkaConfig;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import io.dropwizard.Configuration;
import javax.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

/**
 * Configuration parameters for dataFascia API server
 */
@JsonAutoDetect
public class APIConfiguration extends Configuration {
  @NotNull @Getter @Setter
  private AccumuloConfig accumuloConfig;

  @NotNull @Getter @Setter
  private KafkaConfig kafkaConfig;
}
