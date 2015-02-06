// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.accumulo.minicluster.MiniAccumuloConfig;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Import date to Accumulo test
 */
@Slf4j
public class AccumuloImportTest {
  private File tempDir;
  private MiniAccumuloCluster mac;
  private AccumuloConfiguration config;
  private Injector injector;

  @BeforeClass
  public void setup() throws IOException, InterruptedException {
    tempDir = Files.createTempDir();
    mac = new MiniAccumuloCluster(new MiniAccumuloConfig(tempDir, AccumuloConfiguration.TESTING_PASSWORD));
    mac.start();

    config = new AccumuloConfiguration() {{
        setInstance(mac.getInstanceName());
        setUser(AccumuloConfiguration.TESTING_USER);
        setPassword(AccumuloConfiguration.TESTING_PASSWORD);
        setZooKeepers(mac.getZooKeepers());
        setType(AccumuloConfiguration.MINI);
        }};
    injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(AccumuloConfiguration.class).toInstance(config);
      }}, new AccumuloConnector());
  }

  @AfterClass
  public void teardown() throws Exception {
    mac.stop();
    FileUtils.deleteDirectory(tempDir);
  }

  // Test class for connector injection
  static class TestClass {
    @Inject
    Connector connect;
  }

  @Test
  public void importData() throws AccumuloException, AccumuloSecurityException, IOException,
      TableExistsException, TableNotFoundException, InterruptedException {
    // Give a little time for mini-cluster to start
    Thread.sleep(9000);

    TestClass test = injector.getInstance(TestClass.class);
    String resourceFile = Resources.getResource("sample.rf").getPath();
    String path = resourceFile.substring(0,resourceFile.lastIndexOf(File.separator));
    AccumuloImport.importData(test.connect, "test", path, Files.createTempDir().getPath());
  }
}
