// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import java.io.File;
import java.io.IOException;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Import data to Accumulo test
 */
public class AccumuloImportTest {

  private static class TestModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public Connector connector(ConnectorFactory factory) {
      return factory.getConnector();
    }

    @Provides
    @Singleton
    public ConnectorFactory connectorFactory() {
      return new ConnectorFactory(AccumuloConfiguration.builder()
          .instance(ConnectorFactory.MOCK_INSTANCE)
          .zooKeepers("")
          .user("root")
          .password("secret")
          .build());
    }
  }

  private Connector connector;

  @BeforeClass
  public void setup() throws IOException, InterruptedException {
    Injector injector = Guice.createInjector(new TestModule());

    connector = injector.getInstance(Connector.class);
  }

  @Test
  public void importData() throws Exception {
    String resourceFile = Resources.getResource("accumulo-data/sample.rf").getPath();
    String path = resourceFile.substring(0, resourceFile.lastIndexOf(File.separator));
    AccumuloImport.importData(connector, "test", path, Files.createTempDir().getPath());
  }
}
