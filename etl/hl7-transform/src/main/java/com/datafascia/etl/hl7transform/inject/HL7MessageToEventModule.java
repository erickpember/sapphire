// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform.inject;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.parser.Parser;
import com.codahale.metrics.MetricRegistry;
import com.datafascia.common.accumulo.AuthorizationsProvider;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedAuthorizationsProvider;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
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
    bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
    bind(HapiContext.class).toInstance(new DefaultHapiContext());
    bind(MetricRegistry.class).in(Singleton.class);
  }

  @Provides @Singleton
  public Connector getConnector(ConnectorFactory connectorFactory) {
    return connectorFactory.getConnector();
  }

  @Provides @Singleton
  public Parser getParser(HapiContext context) {
    return context.getGenericParser();
  }
}
