// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.inject;

import com.codahale.metrics.MetricRegistry;
import com.datafascia.accumulo.AuthorizationsProvider;
import com.datafascia.accumulo.ConnectorFactory;
import com.datafascia.accumulo.FixedAuthorizationsProvider;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.google.inject.Provides;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;

/**
 * Provides objects to application.
 */
public class HL7MessageToEventModule extends ConfigureModule {

  @Override
  protected void onConfigure() {
    bind(AuthorizationsProvider.class).to(FixedAuthorizationsProvider.class);
  }

  @Provides @Singleton
  public Connector getConnector(ConnectorFactory connectorFactory) {
    return connectorFactory.getConnector();
  }

  @Provides @Singleton
  public MetricRegistry getMetricRegistry() {
    return new MetricRegistry();
  }
}
