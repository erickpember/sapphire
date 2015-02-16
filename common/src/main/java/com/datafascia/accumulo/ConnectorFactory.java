// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

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
@Slf4j
public class ConnectorFactory {

  /** pass this instance name to create a mock instance */
  public static final String MOCK_INSTANCE = "mock";

  private static final Authorizations AUTHORIZATIONS = new Authorizations("System");

  private final String instance;
  private final String zooKeepers;
  private final String user;
  private final String password;

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
        new Object[] { instance, zooKeepers, user });
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
