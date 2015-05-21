// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.process.inject;

import com.codahale.metrics.MetricRegistry;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedAuthorizationsSupplier;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.persist.entity.AccumuloReflectEntityStore;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.domain.persist.Tables;
import com.google.inject.Provides;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;

/**
 * Provides objects to application.
 */
public class ProcessEventModule extends ConfigureModule {

  @Override
  protected void onConfigure() {
    bind(AuthorizationsSupplier.class).to(FixedAuthorizationsSupplier.class);
    bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
    bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
    bind(MetricRegistry.class).in(Singleton.class);
  }

  @Provides @Singleton
  public Connector connector(ConnectorFactory connectorFactory) {
    return connectorFactory.getConnector();
  }

  @Provides @Singleton
  public ReflectEntityStore entityStore(
      AvroSchemaRegistry schemaRegistry, AccumuloTemplate accumuloTemplate) {

    return new AccumuloReflectEntityStore(schemaRegistry, Tables.ENTITY_PREFIX, accumuloTemplate);
  }
}
