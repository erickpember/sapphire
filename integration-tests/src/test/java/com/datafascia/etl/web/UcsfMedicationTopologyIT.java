// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.web;

import backtype.storm.Config;
import backtype.storm.LocalCluster;
import backtype.storm.generated.StormTopology;
import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.TopologyBuilder;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Tuple;
import com.datafascia.api.services.ApiIT;
import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.WireMock;
import com.google.common.io.Resources;
import java.io.File;
import java.io.FileInputStream;
import java.security.KeyStore;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import javax.net.ssl.SSLContext;
import lombok.extern.slf4j.Slf4j;
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
import static org.testng.Assert.assertTrue;

/**
 * Test for the UCSF medication topology.
 */
@Slf4j
public class UcsfMedicationTopologyIT {
  private static final String trustStorePath = toPath("client.jks");
  private static final String trustStorePassword = "secret";
  private static final String keyStorePath = toPath("server.jks");
  private static final String keyStorePassword = "secret";

  public WireMockServer wireMockServer;

  @BeforeClass
  public static void setup() throws Exception {
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

  @Test
  public void testTopology() throws Exception {
    int port = startStandinService();
    AccumuloConfiguration config = ApiIT.getInjector().getInstance(AccumuloConfiguration.class);

    UcsfMedicationSpout spout = new UcsfMedicationSpout(config.getInstance(),
        config.getZooKeepers(), config.getUser(), config.getPassword(), "https://localhost:" + port
        + "/medication", Duration.ofMinutes(5));

    submitTopology(spout);
  }

  public static void submitTopology(UcsfMedicationSpout spout) throws Exception {
    // Topology config
    Config conf = new Config();
    conf.registerMetricsConsumer(backtype.storm.metric.LoggingMetricsConsumer.class, 1);
    conf.setDebug(true);
    conf.setNumWorkers(4);

    // Construct topology
    TopologyBuilder builder = new TopologyBuilder();
    builder.setSpout("spout1", spout);
    // A basic bolt to ensure it receives the expected tuples.
    builder.setBolt("bolt1", new TestBolt()).shuffleGrouping("spout1");
    LocalCluster cluster = new LocalCluster();
    StormTopology topo = builder.createTopology();
    cluster.submitTopology("ucsfMed", conf, topo);

    Instant start = Instant.now();
    while (!TestBolt.tuplesReceived.get()) {
      if (Instant.now().minusMillis(10000).isAfter(start)) {
        assertTrue(false, "Test bolt failed to receive tuples within ten seconds.");
      }
      Thread.sleep(1);
    }

    cluster.killTopology("ucsfMed");
    cluster.shutdown();
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

  private static String toPath(String resourcePath) {
    try {
      return new File(Resources.getResource(resourcePath).toURI()).getCanonicalPath();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @AfterClass
  public void stop() {
    wireMockServer.stop();
  }

  /**
   * A test bolt to ensure reception of the tuples.
   */
  public static class TestBolt extends BaseRichBolt {
    public static AtomicBoolean tuplesReceived = new AtomicBoolean(false);

    @Override
    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
    }

    @Override
    public void execute(Tuple input) {
      tuplesReceived.set(true);
      assertEquals(input.getValues().get(0).toString(),
          "[{\"patient:\":\"urn:df-patientId-1:urn%3Adf-institution-patientId-1%3AUCSF%3A%3A96087" +
              "004\",\"encounter\":\"UCSF |  | 039ae46a-20a1-4bcd-abb9-68e38d4222c0\",\"medicatio" +
              "ns\":[{\"test\":\"test\"}]}, {\"patient:\":\"urn:df-patientId-1:urn%3Adf-instituti" +
              "on-patientId-1%3AUCSF%3A%3A96087039\",\"encounter\":\"UCSF |  | 0728eb62-2f16-484f" +
              "-8628-a320e99c635d\",\"medications\":[{\"test\":\"test\"}]}]");
    }

    @Override
    public void declareOutputFields(OutputFieldsDeclarer declarer) {
    }
  }
}
