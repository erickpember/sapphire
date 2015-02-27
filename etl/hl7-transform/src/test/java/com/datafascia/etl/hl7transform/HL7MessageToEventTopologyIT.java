// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import backtype.storm.ILocalCluster;
import backtype.storm.Testing;
import backtype.storm.testing.TestJob;
import backtype.storm.tuple.Values;
import com.datafascia.accumulo.AccumuloConfiguration;
import com.datafascia.accumulo.AuthorizationsProvider;
import com.datafascia.accumulo.ConnectorFactory;
import com.datafascia.accumulo.FixedAuthorizationsProvider;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.inject.Injectors;
import com.datafascia.common.storm.trident.StreamFactory;
import com.datafascia.domain.model.IngestMessage;
import com.datafascia.domain.persist.IngestMessageDao;
import com.datafascia.domain.persist.Tables;
import com.google.inject.Injector;
import com.google.inject.Provides;
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

  private static class TestModule extends ConfigureModule {
    @Override
    protected void onConfigure() {
      bind(AuthorizationsProvider.class).to(FixedAuthorizationsProvider.class);
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
    public EventProducer getEventProducer() {
      return HL7MessageToEventTopologyIT.eventProducer;
    }
  }

  private static final String TOPIC = "hl7Message";
  private static final String HL7_MESSAGE_SPOUT_ID = "hl7MessageSpout";
  private static final String PAYLOAD =
      "MSH|^~\\&|APeX|UCSF|IE|UCSF|20141001120532|ADTPA|ADT^A01|48213|T|2.4\r" +
      "PID|1||97546762||NATUS-ADULT^ONE^A||19841001|F|NATUSADULT^ONE^A|U" +
      "|123 MAIN^^SAN FRANCISCO^CA^94122^000^P||(415)999-9999^P^PH^^^415^9999999||ENG|S||41005438" +
      "|888-88-8888|||U||||||||N\r" +
      "ROL|1||General|14852^ROTH^DANIEL^ELI^^^^^EPIC^^^^PROVID|201410010000||||GENERAL|EXTERNAL" +
      "|1 SHRADER ST #578^^SAN FRANCISCO^CA^94117" +
      "|(415)876-5762^^PH^^^415^8765762~(415)876-4538^^FX^^^415^8764538\r" +
      "PV1|1|I|A6A^A6597^28^5102^D^^^^^^OUTADT|R||" +
      "|51112^CUCINA^RUSSELL^J^^^^^EPIC^^^^PROVID~025888116^CUCINA^RUSSELL^J" +
      "|51112^CUCINA^RUSSELL^J^^^^^EPIC^^^^PROVID~025888116^CUCINA^RUSSELL^J||Hosp Med||||ACC||" +
      "|51112^CUCINA^RUSSELL^J^^^^^EPIC^^^^PROVID~025888116^CUCINA^RUSSELL^J|IP|5014212|AETNA||||" +
      "|||||||||||||||||AD|^^^5102||20141001120100\r";

  private static ConnectorFactory connectorFactory;
  private static Injector injector;
  private static Connector connector;
  private static IngestMessageDao ingestMessageDao;
  private static EventProducer eventProducer;

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

    eventProducer = mock(EventProducer.class);

    Injectors.overrideWith(new TestModule());
    injector = Injectors.getInjector();

    connector = injector.getInstance(Connector.class);
  }

  @BeforeMethod
  public void beforeMethod() throws Exception {
    connector.tableOperations().create(Tables.INGEST_MESSAGE);

    ingestMessageDao = injector.getInstance(IngestMessageDao.class);

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
        .payload(ByteBuffer.wrap(PAYLOAD.getBytes(StandardCharsets.UTF_8)))
        .build();
    feedHL7Message(originalMessage);

    Optional<IngestMessage> optionalMessage = ingestMessageDao.read(originalMessage.getId());
    assertTrue(optionalMessage.isPresent());

    IngestMessage message = optionalMessage.get();
    assertEquals(message.getTimestamp(), originalMessage.getTimestamp());
    assertEquals(message.getPayload(), originalMessage.getPayload());

    verify(eventProducer).send(any());
  }

  private void feedHL7Message(IngestMessage message) {
    feedHL7Message(serializer.encodeReflect(TOPIC, message));
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
