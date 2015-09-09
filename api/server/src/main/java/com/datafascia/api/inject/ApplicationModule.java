// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.inject;

import ca.uhn.fhir.context.FhirContext;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.persist.entity.AccumuloFhirEntityStore;
import com.datafascia.common.persist.entity.AccumuloReflectEntityStore;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.domain.persist.Tables;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;

/**
 * Provides objects to application.
 */
public class ApplicationModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
    bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
    bind(FhirContext.class).in(Singleton.class);
    bind(FhirEntityStore.class).to(AccumuloFhirEntityStore.class).in(Singleton.class);
    bind(ReflectEntityStore.class).to(AccumuloReflectEntityStore.class).in(Singleton.class);

    bindConstant().annotatedWith(Names.named("entityTableNamePrefix")).to(Tables.ENTITY_PREFIX);
  }

  @Provides
  @Singleton
  public Connector connector(ConnectorFactory factory) {
    return factory.getConnector();
  }

  @Provides
  @Singleton
  public ConnectorFactory connectorFactory(AccumuloConfiguration accumuloConfiguration) {
    return new ConnectorFactory(accumuloConfiguration);
  }
}
