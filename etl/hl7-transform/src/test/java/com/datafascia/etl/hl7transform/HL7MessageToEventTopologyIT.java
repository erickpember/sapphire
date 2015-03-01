// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import backtype.storm.ILocalCluster;
import backtype.storm.Testing;
import backtype.storm.testing.TestJob;
import backtype.storm.tuple.Values;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedAuthorizationsSupplier;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.inject.Injectors;
import com.datafascia.common.storm.trident.StreamFactory;
import com.datafascia.domain.model.IngestMessage;
import com.datafascia.domain.persist.IngestMessageRepository;
import com.datafascia.domain.persist.Tables;
import com.datafascia.kafka.SingleTopicProducer;
import com.google.common.io.Resources;
import com.google.inject.Injector;
import com.google.inject.Provides;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.Optional;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import storm.trident.Stream;
import storm.trident.TridentTopology;
import storm.trident.testing.FeederBatchSpout;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * {@link HL7MessageToEventTopology} integration test
 */
@Test(singleThreaded = true)
public class HL7MessageToEventTopologyIT {

  private static class TestEventProducer extends EventProducer {
    public TestEventProducer(AvroSchemaRegistry schemaRegistry, Serializer serializer) {
      super(schemaRegistry, serializer);
      topic = EVENT_TOPIC;
    }

    @Override
    protected SingleTopicProducer createProducer() {
      return singleTopicProducer;
    }
  }

  private static class TestModule extends ConfigureModule {
    @Override
    protected void onConfigure() {
      bind(AuthorizationsSupplier.class).to(FixedAuthorizationsSupplier.class);
      bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
    }

    @Provides
    public ConnectorFactory getConnectorFactory() {
      return HL7MessageToEventTopologyIT.connectorFactory;
    }

    @Provides @Singleton
    public Connector getConnector(ConnectorFactory factory) {
      return factory.getConnector();
    }

    @Provides
    public EventProducer getEventProducer(
        AvroSchemaRegistry schemaRegistry, Serializer serializer) {

      return new TestEventProducer(schemaRegistry, serializer);
    }
  }

  private static final String HL7_MESSAGE_TOPIC = "hl7Message";
  private static final String HL7_MESSAGE_SPOUT_ID = "hl7MessageSpout";
  private static final String EVENT_TOPIC = "event";

  private static ConnectorFactory connectorFactory;
  private static Injector injector;
  private static Connector connector;
  private static IngestMessageRepository ingestMessageRepository;
  private static SingleTopicProducer singleTopicProducer;

  private HL7MessageToEventTopology topology;
  private FeederBatchSpout hl7MessageSpout;
  private Serializer serializer;

  @BeforeClass
  public void beforeClass() {
    connectorFactory = new ConnectorFactory(AccumuloConfiguration.builder()
        .instance(ConnectorFactory.MOCK_INSTANCE)
        .zooKeepers("")
        .user("root")
        .password("")
        .build());

    Injectors.overrideWith(new TestModule());
    injector = Injectors.getInjector();

    connector = injector.getInstance(Connector.class);

    singleTopicProducer = mock(SingleTopicProducer.class);
  }

  @BeforeMethod
  public void beforeMethod() throws Exception {
    connector.tableOperations().create(Tables.INGEST_MESSAGE);

    ingestMessageRepository = injector.getInstance(IngestMessageRepository.class);

    topology = new HL7MessageToEventTopology();

    hl7MessageSpout = new FeederBatchSpout(Arrays.asList(F.BYTES));
    topology.setHL7MessageStreamFactory(new StreamFactory() {
      @Override
      public Stream newStream(TridentTopology topology) {
        return topology.newStream(HL7_MESSAGE_SPOUT_ID, hl7MessageSpout);
      }
    });

    serializer = new Serializer(injector.getInstance(AvroSchemaRegistry.class));
  }

  @AfterMethod
  public void afterMethod() throws Exception {
    connector.tableOperations().delete(Tables.INGEST_MESSAGE);
  }

  @Test
  public void should_save_message() throws Exception {
    IngestMessage originalMessage = IngestMessage.builder()
        .timestamp(Instant.now())
        .institution(URI.create("urn:df-institution:institution"))
        .facility(URI.create("urn:df-facility:facility"))
        .payloadType(URI.create("urn:df-payloadtype:HL7v2"))
        .payload(getPayload())
        .build();
    feedHL7Message(originalMessage);

    Optional<IngestMessage> optionalMessage = ingestMessageRepository.read(originalMessage.getId());
    assertTrue(optionalMessage.isPresent());

    IngestMessage message = optionalMessage.get();
    assertEquals(message.getTimestamp(), originalMessage.getTimestamp());
    assertEquals(message.getPayload(), originalMessage.getPayload());

    verify(singleTopicProducer).send(any());
  }

  private ByteBuffer getPayload() throws IOException {
    String payload = Resources.toString(
        Resources.getResource("ADT_A01.hl7"), StandardCharsets.UTF_8);
    payload = payload.replace('\n', '\r');
    return ByteBuffer.wrap(payload.getBytes(StandardCharsets.UTF_8));
  }

  private void feedHL7Message(IngestMessage message) {
    feedHL7Message(serializer.encodeReflect(HL7_MESSAGE_TOPIC, message));
  }

  private void feedHL7Message(final byte[] bytes) {
    Testing.withLocalCluster(new TestJob() {
      @Override
      public void run(ILocalCluster cluster) throws Exception {
        cluster.submitTopology(
            HL7MessageToEventTopology.class.getSimpleName(),
            topology.configureTopology(),
            topology.buildTopology());

        hl7MessageSpout.feed(new Values(Arrays.asList(bytes)));
      }
    });
  }
}
