// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.inject;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import com.codahale.metrics.MetricRegistry;
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
import com.datafascia.domain.persist.Tables;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.name.Names;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;

/**
 * Provides objects to application.
 */
public class ComponentsModule extends AbstractModule {

  @Override
  protected void configure() {
    bind(AuthorizationsSupplier.class).to(FixedAuthorizationsSupplier.class);
    bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
    bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
    bind(FhirContext.class).in(Singleton.class);
    bind(FhirEntityStore.class).to(AccumuloFhirEntityStore.class).in(Singleton.class);
    bind(MetricRegistry.class).in(Singleton.class);

    bindConstant().annotatedWith(Names.named("entityTableNamePrefix")).to(Tables.ENTITY_PREFIX);
  }

  @Provides @Singleton
  public Connector connector(ConnectorFactory connectorFactory) {
    return connectorFactory.getConnector();
  }

  @Provides @Singleton
  public HapiContext hapiContext() {
    HapiContext context = new DefaultHapiContext();

    /* HL7 v2 is a backwards compatible standard for the most part. It is
     * possible to use a HAPI message structure to parse a message of the same
     * type from an earlier version of the standard. Force a specific HL7
     * version to use. Choose the highest HL7 version we need to support, and
     * the model classes will be compatible with messages from previous
     * versions.
     */
    context.setModelClassFactory(new CanonicalModelClassFactory("2.4"));

    return context;
  }

  @Provides @Singleton
  public Parser parser(HapiContext context) {
    return context.getPipeParser();
  }

  @Provides @Singleton
  public ReflectEntityStore reflectEntityStore(
      AvroSchemaRegistry schemaRegistry, AccumuloTemplate accumuloTemplate) {

    return new AccumuloReflectEntityStore(schemaRegistry, Tables.ENTITY_PREFIX, accumuloTemplate);
  }
}
