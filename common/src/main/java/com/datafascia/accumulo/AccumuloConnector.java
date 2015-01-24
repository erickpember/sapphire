// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.datafascia.accumulo.MiniAccumuloStart;
import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.mock.MockInstance;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.client.ZooKeeperInstance;

/**
 * This class provides the Accumulo connector needed by other applications via Guice injection
 * mechanism.
 *
 * NOTE: The Accumulo configuration parameters are injected into this class.
 */
@Slf4j
public class AccumuloConnector extends AbstractModule {
 /**
   * This creates the Accumulo connector. It can be used to create both mock and real instances of
   * Accumulo connectors
   *
   * @return an instance of Accumulo connector
   */
  @Inject @Provides @Singleton
  public Connector connector(AccumuloConfig config) throws AccumuloException,
      AccumuloSecurityException {
    if (config.isMock()) {
      return mockInstance(config);
    }

    return realInstance(config);
  }

  @Override
  public void configure() {
  }

  /**
   * Return connector to real instance of Accumulo.
   *
   * @param config the accumulo configuration
   */
  private Connector realInstance(AccumuloConfig config) throws AccumuloException,
      AccumuloSecurityException {
    log.info("Accumulo Zookeeper: " + config.getZooKeepers() + ", connector for: " +
        config.getInstance() + ", with user " + config.getUser());

    return new ZooKeeperInstance(config.getInstance(),
        config.getZooKeepers()).getConnector(config.getUser(),
        new PasswordToken(config.getPassword()));
  }

  /**
   * Return connector to mock instance of Accumulo. To be used for testing purposes.
   *
   * @param config the accumulo configuration
   */
  private Connector mockInstance(AccumuloConfig config) throws AccumuloException,
      AccumuloSecurityException {
    log.info("Accumulo Mock connector with user: " + config.getUser());

    return new MockInstance().getConnector(config.getUser(),
        new PasswordToken(config.getPassword()));
  }

  /**
   * Return connector to mini instance of Accumulo. To be used for testing purposes.
   *
   * @param config the accumulo configuration
   */
  private Connector miniInstance(AccumuloConfig config) throws AccumuloException,
      AccumuloSecurityException {
    Properties props = new Properties();
    try {
      props.load(new FileInputStream(MiniAccumuloStart.config));
    } catch (IOException e) {
      throw new AccumuloException("Mini-cluster config file missing.", e);
    }
    log.info("Accumulo Mini connector with user: " + props.getProperty(MiniAccumuloStart.USER));

    return new ZooKeeperInstance(props.getProperty(MiniAccumuloStart.INSTANCE),
        props.getProperty(MiniAccumuloStart.ZOOKEEPER)).getConnector(
        props.getProperty(MiniAccumuloStart.USER),
        new PasswordToken(props.getProperty(MiniAccumuloStart.PASSWORD)));
  }
}
