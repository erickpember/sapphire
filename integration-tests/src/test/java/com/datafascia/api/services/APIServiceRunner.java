// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.dropwizard.testing.DropwizardTestApp;
import com.google.common.io.Resources;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Application that runs API server connecting to integration test Accumulo instance
 */
@Slf4j
public class APIServiceRunner {

  private static String getConfigurationFileName() {
    String zooKeepers = TestAccumuloInstance.getZooKeepers();
    System.setProperty("dw.accumulo.zooKeepers", zooKeepers);
    System.setProperty("dw.kafkaConfig.zookeeperConnect", zooKeepers);

    return Resources.getResource("api-server.yml").getFile();
  }

  /**
   * Runs application.
   *
   * @param args
   *     command line arguments
   */
  public static void main(String[] args) throws Exception {
    DropwizardTestApp<APIConfiguration> application = new DropwizardTestApp<>(
        APIService.class, getConfigurationFileName());
    application.start();
    log.info("Started Dropwizard application listening on port {}", application.getLocalPort());

    TimeUnit.HOURS.sleep(1);
  }
}
