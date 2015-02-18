// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertNotNull;

/**
 * {@link AccumuloModule} test
 */
@Slf4j
public class AccumuloModuleTest {
  private static final String USER = "root";
  private static final String PASSWORD = "secret";

  private Injector injector;

  @BeforeClass
  public void beforeClass() {
    injector = Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
          }

          @Provides
          public AccumuloConfiguration getAccumuloConfiguration() {
            return AccumuloConfiguration.builder()
                .instance(ConnectorFactory.MOCK_INSTANCE)
                .zooKeepers("zookeeper1.datafascia.com")
                .user(USER)
                .password(PASSWORD)
                .build();
          }
        },
        new AccumuloModule());
  }

  @Test
  public void should_get_connector() {
    Connector connector = injector.getInstance(Connector.class);
    assertNotNull(connector);
  }
}
