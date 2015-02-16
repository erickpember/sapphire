// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;

/**
 * Guice module that provides the Accumulo connector needed by applications.  The Accumulo
 * configuration parameters are injected into this module.
 */
@Slf4j
public class AccumuloModule extends AbstractModule {

  @Override
  public void configure() {
  }

  @Provides @Singleton
  public Connector getConnector(ConnectorFactory factory) {
    return factory.getConnector();
  }

  @Provides @Singleton
  public ConnectorFactory getConnectorFactory(AccumuloConfiguration accumuloConfiguration) {
    return new ConnectorFactory(accumuloConfiguration);
  }
}
