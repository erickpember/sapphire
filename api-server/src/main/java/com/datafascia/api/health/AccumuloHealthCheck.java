// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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

    if (tServers.size() == 0) {
      return Result.unhealthy("Accumulo connection shaky or running mock instance.");
    }

    return Result.healthy(Joiner.on("; ").join(tServers));
  }
}
