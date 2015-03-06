// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist;

import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AccumuloModule;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.ConnectorFactory;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.accumulo.SubjectAuthorizationsSupplier;
import com.datafascia.common.shiro.FakeRealm;
import com.datafascia.common.shiro.RoleExposingRealm;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Provides;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.admin.TableOperations;
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
 * Base for mock accumulo repository tests.
 */
public class BaseRepositoryTest {
  private static final String USER = "root";
  private static final String PASSWORD = "secret";
  private static ThreadState threadState;
  protected AccumuloTemplate accumuloTemplate;

  @BeforeSuite
  public void beforeClass() throws TableExistsException, AccumuloSecurityException,
      AccumuloException {
    Injector injector = setupMock();
    setupPatientsTable(injector);
    setupMockSecurity();
    accumuloTemplate = injector.getInstance(AccumuloTemplate.class);
  }

  private Injector setupMock() {
    Injector injector = Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(AuthorizationsSupplier.class).to(SubjectAuthorizationsSupplier.class);
            bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
            bind(RoleExposingRealm.class).to(FakeRealm.class);
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

  private void setupMockSecurity() {
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

  @AfterSuite
  public void afterTest() throws Exception {
    threadState.clear();
  }
}
