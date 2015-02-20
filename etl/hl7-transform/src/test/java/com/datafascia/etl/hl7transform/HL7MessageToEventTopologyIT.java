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
import com.datafascia.common.configuration.guice.ConfigureModule;
import com.datafascia.common.inject.Injectors;
import com.datafascia.common.shiro.FakeRealm;
import com.datafascia.common.shiro.RoleExposingRealm;
import com.datafascia.common.storm.trident.StreamFactory;
import com.datafascia.domain.model.IngestMessage;
import com.datafascia.domain.persist.MessageDao;
import com.datafascia.domain.persist.Tables;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.inject.Provides;
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
      bind(RoleExposingRealm.class).to(FakeRealm.class);
    }

    @Provides
    public ConnectorFactory getConnectorFactory() {
      return HL7MessageToEventTopologyIT.connectorFactory;
    }

    @Provides @Singleton
    public Connector getConnector(ConnectorFactory factory) {
      return factory.getConnector();
    }
  }

  private static final String HL7_MESSAGE_SPOUT_ID = "hl7MessageSpout";
  private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
  private static final String PAYLOAD = "MSH";

  private static ConnectorFactory connectorFactory;
  private static Connector connector;
  private static MessageDao messageDao;

  private HL7MessageToEventTopology topology;
  private FeederBatchSpout hl7MessageSpout;

  @BeforeClass
  public void beforeClass() {
    connectorFactory = new ConnectorFactory(AccumuloConfiguration.builder()
        .instance(ConnectorFactory.MOCK_INSTANCE)
        .zooKeepers("")
        .user("root")
        .password("")
        .build());

    Injectors.overrideWith(new TestModule());
    connector = Injectors.getInjector().getInstance(Connector.class);
  }

  @BeforeMethod
  public void beforeMethod() throws Exception {
    connector.tableOperations().create(Tables.MESSAGE);

    messageDao = Injectors.getInjector().getInstance(MessageDao.class);

    topology = new HL7MessageToEventTopology();

    hl7MessageSpout = new FeederBatchSpout(Arrays.asList(F.BYTES));
    topology.setHL7MessageStreamFactory(new StreamFactory() {
      @Override
      public Stream newStream(TridentTopology topology) {
        return topology.newStream(HL7_MESSAGE_SPOUT_ID, hl7MessageSpout);
      }
    });
  }

  @AfterMethod
  public void afterMethod() throws Exception {
    connector.tableOperations().delete(Tables.MESSAGE);
  }

  @Test
  public void should_save_message() throws Exception {
    IngestMessage originalMessage = IngestMessage.builder()
        .timestamp(Instant.now())
        .payload(PAYLOAD)
        .build();
    feedHL7Message(originalMessage);

    Optional<IngestMessage> optionalMessage = messageDao.read(originalMessage.getId());
    assertTrue(optionalMessage.isPresent());

    IngestMessage message = optionalMessage.get();
    assertEquals(message.getTimestamp(), originalMessage.getTimestamp());
    assertEquals(message.getPayload(), originalMessage.getPayload());
  }

  private void feedHL7Message(IngestMessage message) throws JsonProcessingException {
    feedHL7Message(OBJECT_MAPPER.writeValueAsBytes(message));
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
