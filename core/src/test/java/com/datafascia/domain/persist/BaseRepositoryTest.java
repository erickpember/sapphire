// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AccumuloModule;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedAuthorizationsSupplier;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.admin.TableOperations;
import org.testng.annotations.BeforeSuite;

/**
 * Base for mock accumulo repository tests.
 */
public class BaseRepositoryTest {
  private static final String USER = "root";
  private static final String PASSWORD = "secret";
  protected AccumuloTemplate accumuloTemplate;

  @BeforeSuite
  public void beforeClass() throws TableExistsException, AccumuloSecurityException,
      AccumuloException {
    Injector injector = setupMock();
    setupPatientsTable(injector);
    accumuloTemplate = injector.getInstance(AccumuloTemplate.class);
  }

  private Injector setupMock() {
    Injector injector = Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(AuthorizationsSupplier.class).to(FixedAuthorizationsSupplier.class);
            bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
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

    return injector;
  }

  private void setupPatientsTable(Injector injector) throws TableExistsException,
      AccumuloSecurityException, AccumuloException {
    Connector connector = injector.getInstance(Connector.class);
    TableOperations tableOps = connector.tableOperations();
    tableOps.create("Patient");
  }
}
