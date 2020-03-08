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
package com.datafascia.common.accumulo;

import com.datafascia.common.configuration.ConfigurationNode;
import com.datafascia.common.configuration.Configure;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Creates Accumulo connector.
 */
@ConfigurationNode("accumulo") @NoArgsConstructor @Slf4j
public class ConnectorFactory {

  /** pass this instance name to create a mock instance */
  public static final String MOCK_INSTANCE = "mock";

  private static final Authorizations AUTHORIZATIONS = new Authorizations("System");

  @Configure
  private String instance;

  @Configure
  private String zooKeepers;

  @Configure
  private String user;

  @Configure
  private String password;

  private Instance cluster;
  private Connector connector;

  /**
   * Constructor
   *
   * @param accumuloConfiguration
   *     Accumulo configuration settings
   */
  public ConnectorFactory(AccumuloConfiguration accumuloConfiguration) {
    this.instance = accumuloConfiguration.getInstance();
    this.zooKeepers = accumuloConfiguration.getZooKeepers();
    this.user = accumuloConfiguration.getUser();
    this.password = accumuloConfiguration.getPassword();
  }

  /**
   * Gets connector to Accumulo instance.
   *
   * @return connector
   */
  public synchronized Connector getConnector() {
    if (connector == null) {
      connector = createConnector();
    }
    return connector;
  }

  private Connector createConnector() {
    log.info(
        "Connecting to Accumulo, instance [{}], zooKeepers [{}], user [{}]",
        new Object[]{instance, zooKeepers, user});
    try {
      String connectorPassword;
      if (MOCK_INSTANCE.equals(instance)) {
        cluster = new MockInstance();
        connectorPassword = "";
      } else {
        cluster = new ZooKeeperInstance(instance, zooKeepers);
        connectorPassword = password;
      }

      connector = cluster.getConnector(user, new PasswordToken(connectorPassword));

      connector.securityOperations().changeUserAuthorizations(user, AUTHORIZATIONS);
      return connector;
    } catch (AccumuloException | AccumuloSecurityException e) {
      throw new IllegalStateException("createConnector", e);
    }
  }
}
