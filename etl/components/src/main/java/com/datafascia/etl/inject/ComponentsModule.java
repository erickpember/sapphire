// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.inject;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.client.IRestfulClientFactory;
import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.parser.CanonicalModelClassFactory;
import ca.uhn.hl7v2.parser.Parser;
import ca.uhn.hl7v2.validation.impl.NoValidation;
import com.datafascia.api.client.ClientBuilder;
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
import com.datafascia.emerge.ucsf.harm.HarmEvidenceUpdater;
import com.datafascia.etl.event.EncounterExecutor;
import com.datafascia.etl.event.PlayMessages;
import com.datafascia.etl.hl7.EncounterStatusTransition;
import com.datafascia.etl.hl7.HL7MessageProcessor;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.matcher.Matchers;
import com.google.inject.name.Names;
import java.time.Clock;
import java.time.ZoneId;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

/**
 * Provides objects to application.
 */
public class ComponentsModule extends AbstractModule {

  private static final ZoneId ZONE_ID = ZoneId.of("America/Los_Angeles");

  @Override
  protected void configure() {
    // Set default time zone so HAPI will interpret times in this time zone.
    TimeZone.setDefault(TimeZone.getTimeZone(ZONE_ID));

    bindInterceptor(
        Matchers.inPackage(HarmEvidenceUpdater.class.getPackage()),
        Matchers.any(),
        new TracingInterceptor());

    bind(AuthorizationsSupplier.class)
        .to(FixedAuthorizationsSupplier.class)
        .in(Singleton.class);
    bind(AvroSchemaRegistry.class)
        .to(MemorySchemaRegistry.class)
        .in(Singleton.class);
    bind(ClientBuilder.class)
        .in(Singleton.class);
    bind(Clock.class)
        .toInstance(Clock.system(ZONE_ID));
    bind(ColumnVisibilityPolicy.class)
        .to(FixedColumnVisibilityPolicy.class)
        .in(Singleton.class);
    bind(EncounterExecutor.class)
        .in(Singleton.class);
    bind(EncounterStatusTransition.class)
        .in(Singleton.class);
    bind(FhirEntityStore.class)
        .to(AccumuloFhirEntityStore.class)
        .in(Singleton.class);
    bind(HL7MessageProcessor.class)
        .in(Singleton.class);
    bind(HarmEvidenceUpdater.class)
        .in(Singleton.class);
    bind(PlayMessages.class)
        .in(Singleton.class);
    bind(ReflectEntityStore.class)
        .to(AccumuloReflectEntityStore.class)
        .in(Singleton.class);

    bindConstant()
        .annotatedWith(Names.named("entityTableNamePrefix"))
        .to(Tables.ENTITY_PREFIX);
  }

  @Provides @Singleton
  public Connector connector(ConnectorFactory connectorFactory) {
    return connectorFactory.getConnector();
  }

  @Provides @Singleton
  public FhirContext fhirContext() {
    FhirContext fhirContext = FhirContext.forDstu2();

    // The default maximum connections per route and total connections is too small.
    // Configure an HttpClient with increased limits.
    IRestfulClientFactory clientFactory = fhirContext.getRestfulClientFactory();

    PoolingHttpClientConnectionManager connectionManager =
        new PoolingHttpClientConnectionManager(30000, TimeUnit.MILLISECONDS);
    connectionManager.setDefaultMaxPerRoute(20);
    connectionManager.setMaxTotal(200);
    connectionManager.setValidateAfterInactivity(5000);

    RequestConfig defaultRequestConfig = RequestConfig.custom()
        .setSocketTimeout(30000)
        .setConnectTimeout(clientFactory.getConnectTimeout())
        .setConnectionRequestTimeout(clientFactory.getConnectionRequestTimeout())
        .build();

    HttpClientBuilder httpClientBuilder = HttpClients.custom()
        .setConnectionManager(connectionManager)
        .setDefaultRequestConfig(defaultRequestConfig)
        .disableCookieManagement();

    clientFactory.setHttpClient(httpClientBuilder.build());

    return fhirContext;
  }

  @Provides @Singleton
  public HapiContext hapiContext() {
    HapiContext context = new DefaultHapiContext(new NoValidation());

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
}
