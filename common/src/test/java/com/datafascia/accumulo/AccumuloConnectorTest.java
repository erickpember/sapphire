// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Inject;
import com.google.inject.Injector;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Test for Accumulo connector
 */
@Slf4j
public class AccumuloConnectorTest {
  AccumuloConfiguration config = new AccumuloConfiguration() {{
      setInstance("test");
      setUser("user");
      setPassword("password");
      setZooKeepers("zookeeper1.datafascia.com");
      setType(AccumuloConfiguration.MOCK);
      }};
  Injector injector;

  @BeforeClass
  public void setup() {
    injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(AccumuloConfiguration.class).toInstance(config);
      }}, new AccumuloConnector());
  }

  // Test class for connector injection
  static class TestClass {
    @Inject
    Connector connect;
  }

  @Test
  public void mockConnector() {
    TestClass test = injector.getInstance(TestClass.class);
  }
}
