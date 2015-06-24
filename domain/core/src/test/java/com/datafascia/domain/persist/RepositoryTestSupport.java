// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import ca.uhn.fhir.context.FhirContext;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AccumuloTemplate;
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
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.testng.annotations.BeforeClass;

/**
 * Base for mock Accumulo repository tests.
 */
public abstract class RepositoryTestSupport {

  /**
   * Provides test dependencies
   */
  private static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(AuthorizationsSupplier.class).to(FixedAuthorizationsSupplier.class);
      bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
      bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
      bind(FhirContext.class).in(Singleton.class);
      bind(FhirEntityStore.class).to(AccumuloFhirEntityStore.class).in(Singleton.class);

      bindConstant().annotatedWith(Names.named("entityTableNamePrefix")).to(Tables.ENTITY_PREFIX);
    }

    @Provides @Singleton
    public Connector connector(ConnectorFactory factory) {
      return factory.getConnector();
    }

    @Provides @Singleton
    public ConnectorFactory connectorFactory() {
      return new ConnectorFactory(AccumuloConfiguration.builder()
          .instance(ConnectorFactory.MOCK_INSTANCE)
          .zooKeepers("")
          .user("root")
          .password("secret")
          .build());
    }

    @Provides @Singleton
    public ReflectEntityStore reflectEntityStore(
        AvroSchemaRegistry schemaRegistry, AccumuloTemplate accumuloTemplate) {

      return new AccumuloReflectEntityStore(schemaRegistry, Tables.ENTITY_PREFIX, accumuloTemplate);
    }
  }

  @Inject
  private AccumuloTemplate accumuloTemplate;

  @Inject
  private AccumuloReflectEntityStore entityStore;

  @BeforeClass
  public void beforeRepositoryTestSupport() throws Exception {
    Injector injector = Guice.createInjector(new TestModule());
    injector.injectMembers(this);
  }

  protected void scan() {
    Scanner scanner = accumuloTemplate.createScanner(entityStore.getDataTableName());
    try {
      for (Map.Entry<Key, Value> entry : scanner) {
        System.out.format(
            "%s %s %s %s\n",
            entry.getKey().getRow(),
            entry.getKey().getColumnFamily(),
            entry.getKey().getColumnQualifier(),
            entry.getValue());
      }
    } finally {
      scanner.close();
    }
  }
}
