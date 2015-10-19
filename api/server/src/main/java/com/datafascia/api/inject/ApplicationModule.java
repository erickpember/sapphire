// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.inject;

import ca.uhn.fhir.context.FhirContext;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.accumulo.SubjectAuthorizationsSupplier;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.kafka.KafkaConfig;
import com.datafascia.common.persist.entity.AccumuloFhirEntityStore;
import com.datafascia.common.persist.entity.AccumuloReflectEntityStore;
import com.datafascia.common.persist.entity.FhirEntityStore;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.common.shiro.FakeRealm;
import com.datafascia.common.shiro.RoleExposingRealm;
import com.datafascia.domain.persist.Tables;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import io.dropwizard.setup.Environment;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;
import org.apache.shiro.realm.Realm;

/**
 * Provides objects to application.
 */
public class ApplicationModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(AuthorizationsSupplier.class)
        .to(SubjectAuthorizationsSupplier.class);
    bind(AvroSchemaRegistry.class)
        .to(MemorySchemaRegistry.class).in(Singleton.class);
    bind(ColumnVisibilityPolicy.class)
        .to(FixedColumnVisibilityPolicy.class);
    bind(FhirContext.class)
        .toInstance(FhirContext.forDstu2());
    bind(FhirEntityStore.class)
        .to(AccumuloFhirEntityStore.class)
        .in(Singleton.class);
    bind(ReflectEntityStore.class)
        .to(AccumuloReflectEntityStore.class)
        .in(Singleton.class);
    RoleExposingRealm realm = new FakeRealm();
    bind(Realm.class)
        .toInstance(realm);
    bind(RoleExposingRealm.class)
        .toInstance(realm);

    bindConstant().annotatedWith(Names.named("entityTableNamePrefix")).to(Tables.ENTITY_PREFIX);
  }

  @Provides
  public AccumuloConfiguration accumuloConfiguration(APIConfiguration configuration) {
    return configuration.getAccumuloConfiguration();
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

  @Provides
  public KafkaConfig kafkaConfig(APIConfiguration configuration) {
    return configuration.getKafkaConfig();
  }

  @Provides
  public ObjectMapper objectMapper(Environment environment) {
    return environment.getObjectMapper();
  }
}
