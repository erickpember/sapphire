// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import ca.uhn.fhir.context.FhirContext;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedAuthorizationsSupplier;
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
import java.time.Clock;
import java.time.ZoneId;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;

/**
 * A Guice module to support integration tests.
 */
class TestModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(AuthorizationsSupplier.class)
        .to(FixedAuthorizationsSupplier.class)
        .in(Singleton.class);
    bind(AvroSchemaRegistry.class)
        .to(MemorySchemaRegistry.class)
        .in(Singleton.class);
    bind(Clock.class)
        .toInstance(Clock.system(ZoneId.of("America/Los_Angeles")));
    bind(ColumnVisibilityPolicy.class)
        .to(FixedColumnVisibilityPolicy.class)
        .in(Singleton.class);
    bind(FhirContext.class)
        .toInstance(FhirContext.forDstu2());
    bind(FhirEntityStore.class)
        .to(AccumuloFhirEntityStore.class)
        .in(Singleton.class);
    bind(ReflectEntityStore.class)
        .to(AccumuloReflectEntityStore.class)
        .in(Singleton.class);

    bindConstant()
        .annotatedWith(Names.named("entityTableNamePrefix"))
        .to(Tables.ENTITY_PREFIX);
  }

  @Provides
  @Singleton
  public Connector connector(ConnectorFactory factory) {
    return factory.getConnector();
  }

  @Provides
  @Singleton
  public ConnectorFactory connectorFactory() {
    return new ConnectorFactory(TestAccumuloInstance.getConfiguration());
  }
}
