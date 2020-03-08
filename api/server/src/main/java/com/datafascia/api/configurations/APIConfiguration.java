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
package com.datafascia.api.configurations;

import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.kafka.KafkaConfig;
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
