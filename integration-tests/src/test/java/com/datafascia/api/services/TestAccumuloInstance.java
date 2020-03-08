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
