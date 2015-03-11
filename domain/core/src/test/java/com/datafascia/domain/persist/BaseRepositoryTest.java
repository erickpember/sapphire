// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedAuthorizationsSupplier;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.admin.TableOperations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Guice;

/**
 * Base for mock Accumulo repository tests.
 */
@Guice(modules = BaseRepositoryTest.Module.class)
public class BaseRepositoryTest {

  /**
   * Provides test dependencies
   */
  public static class Module extends AbstractModule {
    @Override
    protected void configure() {
      bind(AuthorizationsSupplier.class).to(FixedAuthorizationsSupplier.class);
      bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
    }

    @Provides @Singleton
    public Connector connector(ConnectorFactory factory) {
      return factory.getConnector();
    }

    @Provides @Singleton
    public ConnectorFactory connectorFactory() {
      return new ConnectorFactory(AccumuloConfiguration.builder()
          .instance(ConnectorFactory.MOCK_INSTANCE)
          .zooKeepers("localhost")
          .user("root")
          .password("secret")
          .build());
    }
  }

  @Inject
  private Connector connector;

  @Inject
  protected AccumuloTemplate accumuloTemplate;

  @BeforeClass
  public void beforeClass() throws Exception {
    setupPatientTable();
  }

  private void setupPatientTable() throws Exception {
    TableOperations tableOps = connector.tableOperations();
    tableOps.create("Patient");
  }
}
