// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.common.accumulo.AccumuloConfiguration;
import java.io.File;
import java.io.FileNotFoundException;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.minicluster.MiniAccumuloInstance;

/**
 * Convenience methods to access integration test Accumulo instance
 */
public class TestAccumuloInstance {

  private static final String INSTANCE = "integration-test";

  // Private constructor disallows creating instances of this class.
  private TestAccumuloInstance() {
  }

  /**
   * Gets ZooKeeper hosts and ports.
   *
   * @return ZooKeeper hosts and ports
   */
  public static String getZooKeepers() {
    try {
      Instance instance = new MiniAccumuloInstance(
          INSTANCE,
          new File("target/accumulo-maven-plugin/" + INSTANCE));
      return instance.getZooKeepers();
    } catch (FileNotFoundException e) {
      throw new IllegalStateException("Cannot get Accumulo instance", e);
    }
  }

  /**
   * Gets connection configuration.
   *
   * @return connection configuration
   */
  public static AccumuloConfiguration getConfiguration() {
    return AccumuloConfiguration.builder()
        .instance(INSTANCE)
        .zooKeepers(getZooKeepers())
        .user("root")
        .password("secret")
        .build();
  }
}
