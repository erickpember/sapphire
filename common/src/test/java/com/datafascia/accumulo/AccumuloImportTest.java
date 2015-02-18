// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.accumulo.minicluster.MiniAccumuloConfig;
import org.apache.commons.io.FileUtils;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Import data to Accumulo test
 */
@Slf4j
public class AccumuloImportTest {
  private static final String USER = "root";
  private static final String PASSWORD = "secret";

  private File tempDir;
  private MiniAccumuloCluster cluster;
  private Injector injector;

  @BeforeClass
  public void setup() throws IOException, InterruptedException {
    tempDir = Files.createTempDir();
    cluster = new MiniAccumuloCluster(
        new MiniAccumuloConfig(tempDir, PASSWORD));
    cluster.start();

    injector = Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
          }

          @Provides
          public AccumuloConfiguration getAccumuloConfiguration() {
            return AccumuloConfiguration.builder()
                .instance(cluster.getInstanceName())
                .zooKeepers(cluster.getZooKeepers())
                .user(USER)
                .password(PASSWORD)
                .build();
          }
        },
        new AccumuloModule());
  }

  @AfterClass
  public void teardown() throws Exception {
    cluster.stop();
    FileUtils.deleteDirectory(tempDir);
  }

  @Test
  public void importData() throws Exception {
    // Give a little time for mini-cluster to start
    Thread.sleep(9000);

    Connector connector = injector.getInstance(Connector.class);
    String resourceFile = Resources.getResource("sample.rf").getPath();
    String path = resourceFile.substring(0, resourceFile.lastIndexOf(File.separator));
    AccumuloImport.importData(connector, "test", path, Files.createTempDir().getPath());
  }
}
