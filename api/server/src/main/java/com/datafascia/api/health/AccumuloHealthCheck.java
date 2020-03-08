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
package com.datafascia.api.health;

import com.codahale.metrics.health.HealthCheck;
import com.google.common.base.Joiner;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.admin.InstanceOperations;

/**
 * Health checker for Accumulo connection
 */
@Slf4j
public class AccumuloHealthCheck extends HealthCheck {
  private final Connector connect;

  /**
   * Default constructor for health check
   *
   * @param connect the connection to Accumulo
   */
  @Inject
  public AccumuloHealthCheck(Connector connect) {
    this.connect = connect;
  }

  @Override
  protected Result check() throws Exception {
    InstanceOperations ops = connect.instanceOperations();
    List<String> tServers = ops.getTabletServers();

    if (tServers.isEmpty()) {
      return Result.unhealthy("Accumulo connection shaky or running mock instance.");
    }

    return Result.healthy(Joiner.on("; ").join(tServers));
  }
}
