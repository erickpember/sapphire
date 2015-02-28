// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.accumulo.AccumuloConfiguration;
import com.datafascia.accumulo.AccumuloImport;
import com.datafascia.accumulo.AccumuloModule;
import com.datafascia.accumulo.AuthorizationsProvider;
import com.datafascia.accumulo.SubjectAuthorizationsProvider;
import com.datafascia.api.client.DatafasciaApi;
import com.datafascia.api.client.DatafasciaApiBuilder;
import com.datafascia.api.configurations.APIConfiguration;
import com.datafascia.common.shiro.FakeRealm;
import com.datafascia.common.shiro.RoleExposingRealm;
import com.datafascia.dropwizard.testing.DropwizardTestApp;
import com.datafascia.kafka.KafkaConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.File;
import java.io.FileWriter;
import java.net.URI;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Instance;
import org.apache.accumulo.minicluster.MiniAccumuloInstance;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.support.SubjectThreadState;
import org.apache.shiro.util.Factory;
import org.apache.shiro.util.ThreadState;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeSuite;

/**
 * Integration test for the various API resources
 */
@Slf4j
public class ApiIT {
  protected static final String MODELS_PKG = "com.datafascia.models";
  protected static final String OPAL_TABLE = "opal_dF_data";
  protected static final DateTimeFormatter dateFormat
      = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

  protected static AccumuloConfiguration accConfig;
  protected static KafkaConfig kafkaConfig;
  protected static Injector injector;
  protected static DropwizardTestApp<APIConfiguration> app;
  protected static Connector connect;
  protected static DatafasciaApi api;
  protected static ThreadState threadState;

  /**
   * Prepare the integration test to run.
   *
   * @throws Exception
   */
  @BeforeSuite
  public void before() throws Exception {
    // Delay to allow time for Accumulo mini-cluster to start.
    TimeUnit.SECONDS.sleep(3);

    String configFile = apiConfiguration();
    setupGuice();
    setupShiro();
    connect = injector.getInstance(Connector.class);
    importData();
    log.info("Accumulo populated and ready.");

    log.info("Starting dropwizard app");
    app = new DropwizardTestApp<>(APIService.class, configFile);
    app.start();

    api = DatafasciaApiBuilder.endpoint(
        new URI("http://localhost:" + app.getLocalPort()), "testuser", "supersecret");
  }

  /**
   * Stop the test application.
   *
   * @throws Exception
   */
  @AfterSuite
  public static void after() throws Exception {
    app.stop();
    threadState.clear();
  }

  /**
   * @return the Accumulo configuration to use
   */
  private AccumuloConfiguration accumuloConfig() {
    if (accConfig == null) {
      // Keep the values here in sync with what is in the pom.xml
      String instanceName = "plugin-it-instance";
      try {
        Instance instance = new MiniAccumuloInstance(instanceName,
            new File("target/accumulo-maven-plugin/" + instanceName));
        accConfig = new AccumuloConfiguration();
        accConfig.setInstance(instanceName);
        accConfig.setZooKeepers(instance.getZooKeepers());
        accConfig.setUser("root");
        accConfig.setPassword("supersecret");
      } catch (Exception e) {
        throw new RuntimeException("Error get Accumulo instance.", e);
      }
    }

    return accConfig;
  }

  /**
   * @return the Kafka configuration to use
   */
  private KafkaConfig kafkaConfig() {
    if (kafkaConfig == null) {
      kafkaConfig = new KafkaConfig();
      kafkaConfig.setZookeeperConnect(accumuloConfig().getZooKeepers());
    }

    return kafkaConfig;
  }

  /**
   * @return the API configuration to use as a file
   *
   * @throws Exception should not happen
   */
  private String apiConfiguration() throws Exception {
    ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    ObjectNode node = mapper.createObjectNode();
    node.putPOJO("accumulo", accumuloConfig());
    node.putPOJO("kafkaConfig", kafkaConfig());
    String value = mapper.writeValueAsString(node);

    String configFile = System.getProperty("java.io.tmpdir") + File.separatorChar + "test.yml";
    FileWriter fw = new FileWriter(configFile);
    fw.write(value, 0, value.length());
    fw.close();

    return configFile;
  }

  /**
   * Set up Shiro
   */
  private void setupShiro() {
    Factory<SecurityManager> factory = new IniSecurityManagerFactory("classpath:shiro.ini");
    RealmSecurityManager securityManager = (RealmSecurityManager) factory.getInstance();
    securityManager.setRealm(new FakeRealm());
    SecurityUtils.setSecurityManager(securityManager);

    Subject subject = new Subject.Builder()
        .principals(new SimplePrincipalCollection("root", FakeRealm.class.getSimpleName()))
        .buildSubject();
    threadState = new SubjectThreadState(subject);
    threadState.bind();
  }

  /**
   * Import test data into accumulo
   *
   * @throws Exception from underling Accumulo or IO calls
   */
  private void importData() throws Exception {
    // Find the accumulo data and populate it into our minicluster.
    File failDir = Files.createTempDir();
    String resourceFile = Resources.getResource("version.json").getPath();
    String path = resourceFile.substring(0, resourceFile.lastIndexOf(File.separator));
    AccumuloImport.importData(connect, OPAL_TABLE, path + "/accumulo_data", failDir.getPath());
    failDir.delete();

    connect.tableOperations().create("Patient");
  }

  /**
   * Set up Guice modules
   */
  private void setupGuice() {
    injector = Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(AccumuloConfiguration.class).toInstance(accumuloConfig());
            bind(AuthorizationsProvider.class).to(SubjectAuthorizationsProvider.class);
            bind(RoleExposingRealm.class).to(FakeRealm.class);
          }
        },
        new AccumuloModule());
  }
}
