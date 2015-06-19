// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.web;

import backtype.storm.Config;
import backtype.storm.ILocalCluster;
import backtype.storm.Testing;
import backtype.storm.generated.StormTopology;
import backtype.storm.testing.TestJob;
import backtype.storm.topology.BasicOutputCollector;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseBasicBolt;
import backtype.storm.tuple.Tuple;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedAuthorizationsSupplier;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.common.inject.Injectors;
import com.datafascia.common.persist.entity.AccumuloReflectEntityStore;
import com.datafascia.common.persist.entity.ReflectEntityStore;
import com.datafascia.common.storm.DependencyInjectingBaseRichSpoutProxy;
import com.datafascia.domain.persist.PatientRepository;
import com.datafascia.domain.persist.Tables;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedDeque;
import java.util.concurrent.TimeUnit;
import javax.inject.Inject;
import javax.inject.Singleton;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.minicluster.MiniAccumuloInstance;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.SSLContexts;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.client.WireMock.urlMatching;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.fail;

/**
 * Test for the UCSF medication topology.
 */
@Slf4j
public class UcsfMedicationTopologyIT {
  private static final String trustStorePath = "/tmp/datafascia-test/client.jks";
  private static final String trustStorePassword = "secret";
  private static final String keyStorePath = "/tmp/datafascia-test/server.jks";
  private static final String keyStorePassword = "secret";

  public WireMockServer wireMockServer;

  /**
   * Provides test dependencies
   */
  private static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
      bind(AuthorizationsSupplier.class).to(FixedAuthorizationsSupplier.class);
      bind(AvroSchemaRegistry.class).to(MemorySchemaRegistry.class).in(Singleton.class);
      bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
    }

    @Provides @Singleton
    public AccumuloConfiguration accumuloConfiguration() {
      String instanceName = "integration-test";
      try {
        Instance instance = new MiniAccumuloInstance(
            instanceName, new File("target/accumulo-maven-plugin/" + instanceName));

        return new AccumuloConfiguration(
            instanceName, instance.getZooKeepers(), "root", "secret");
      } catch (Exception e) {
        e.printStackTrace();
        throw new IllegalStateException("Cannot get Accumulo instance", e);
      }
    }

    @Provides @Singleton
    public Connector connector(ConnectorFactory factory) {
      return factory.getConnector();
    }

    @Provides @Singleton
    public ConnectorFactory connectorFactory(AccumuloConfiguration configuration) {
      return new ConnectorFactory(configuration);
    }

    @Provides @Singleton
    public ReflectEntityStore entityStore(
        AvroSchemaRegistry schemaRegistry, AccumuloTemplate accumuloTemplate) {

      return new AccumuloReflectEntityStore(schemaRegistry, Tables.ENTITY_PREFIX, accumuloTemplate);
    }
  }

  @Inject
  private Connector connector;

  @Inject
  private PatientRepository patientRepository;

  private void setupTls() throws Exception {
    KeyStore trustStore  = KeyStore.getInstance(KeyStore.getDefaultType());
    FileInputStream instream = new FileInputStream(trustStorePath);
    try {
      trustStore.load(instream, trustStorePassword.toCharArray());
    } finally {
      instream.close();
    }

    // Make the default SSL context very permissive.
    SSLContext.setDefault(SSLContexts.custom()
        .loadTrustMaterial(null, new TrustSelfSignedStrategy())
        .loadKeyMaterial(trustStore, trustStorePassword.toCharArray())
        .useTLS()
        .build());
  }

  @BeforeClass
  public void beforeClass() throws Exception {
    Injectors.overrideWith(new TestModule());
    Injectors.getInjector().injectMembers(this);

    setupTls();
  }

  @Test
  public void testTopology() throws Exception {
    int port = startStandinService();

    UcsfMedicationSpout spout = new UcsfMedicationSpout(
        "https://localhost:" + port + "/medication", Duration.ofMinutes(5));

    submitTopology(spout, new TestBolt());

    assertEquals(
        TestBolt.receivedTuples.getFirst().toString(),
        "[{\"patient:\":\"urn:df-patientId-1:urn%3Adf-institution-patientId-1%3AUCSF%3A%3A96087" +
            "004\",\"encounter\":\"UCSF |  | 039ae46a-20a1-4bcd-abb9-68e38d4222c0\",\"medicatio" +
            "ns\":[{\"test\":\"test\"}]}, {\"patient:\":\"urn:df-patientId-1:urn%3Adf-instituti" +
            "on-patientId-1%3AUCSF%3A%3A96087039\",\"encounter\":\"UCSF |  | 0728eb62-2f16-484f" +
            "-8628-a320e99c635d\",\"medications\":[{\"test\":\"test\"}]}]");
  }

  public static void submitTopology(UcsfMedicationSpout spout, TestBolt testBolt) throws Exception {
    // Topology config
    Config config = new Config();
    config.setDebug(true);

    // Construct topology
    TopologyBuilder builder = new TopologyBuilder();
    builder.setSpout(UcsfMedicationSpout.ID, new DependencyInjectingBaseRichSpoutProxy(spout));

    builder.setBolt("bolt1", testBolt).shuffleGrouping(UcsfMedicationSpout.ID);
    StormTopology topology = builder.createTopology();

    Testing.withLocalCluster(new TestJob() {
      @Override
      public void run(ILocalCluster cluster) throws Exception {
        cluster.submitTopology(
            UcsfMedicationTopologyIT.class.getSimpleName(), config, topology);

        Instant start = Instant.now();
        while (TestBolt.receivedTuples.isEmpty()) {
          if (Instant.now().minusMillis(10000).isAfter(start)) {
            fail("Test bolt failed to receive tuples within ten seconds.");
          }
          TimeUnit.SECONDS.sleep(1);
        }
      }
    });
  }

  /**
   * Starts a web service that returns a dummy JSON string.
   * @return The port the web service started on.
   */
  public int startStandinService() throws Exception {
    wireMockServer = new WireMockServer(wireMockConfig()
        .dynamicPort().dynamicHttpsPort()
        .keystorePath(keyStorePath)
        .keystorePassword(keyStorePassword)
        .trustStorePath(trustStorePath)
        .trustStorePassword(trustStorePassword)
        .needClientAuth(true)
        .bindAddress("localhost"));
    wireMockServer.start();
    WireMock.configureFor(wireMockServer.port());
    int medicationServicePort = wireMockServer.httpsPort();

    stubFor(get(urlEqualTo("/httpstest")).willReturn(aResponse().withStatus(200).withBody
        ("test")));
    assertEquals(secureContentFor("https://localhost:" + medicationServicePort +
        "/httpstest"), ("test"));

    stubFor(get(urlMatching(".*")).willReturn(aResponse()
        .withStatus(200)
        .withHeader("Content-Type", "json/application")
        .withBody("[{\"test\":\"test\"}]")));

    return medicationServicePort;
  }

  static String secureContentFor(String url)
      throws Exception {
    CloseableHttpClient httpClient = HttpClients.custom().setSslcontext(SSLContext.getDefault())
        .build();

    HttpResponse response = httpClient.execute(new HttpGet(url));
    String content = EntityUtils.toString(response.getEntity());
    return content;
  }

  @AfterClass
  public void stop() {
    wireMockServer.stop();
  }

  /**
   * A test bolt to ensure reception of the tuples.
   */
  public static class TestBolt extends BaseBasicBolt {
    public static ConcurrentLinkedDeque<List<String>> receivedTuples =
        new ConcurrentLinkedDeque<>();

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }

    @Override
    public void execute(Tuple input, BasicOutputCollector collector) {
      List<String> responses =
          (List<String>) input.getValueByField(UcsfMedicationSpout.MEDICATION_JSON_FIELD);
      receivedTuples.add(responses);
    }
  }
}
