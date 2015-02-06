// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.datafascia.accumulo.AccumuloConfiguration;
import com.datafascia.accumulo.AccumuloConnector;
import com.datafascia.accumulo.AccumuloImport;
import com.datafascia.accumulo.QueryTemplate;
import com.datafascia.common.shiro.FakeRealm;
import com.datafascia.common.shiro.RoleExposingRealm;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.File;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
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
 * Framework for testing daos.
 */
@Slf4j
public abstract class DaoIT {

  private static String OPAL_TABLE = "opal_dF_data";

  private static AccumuloConfiguration config =
      new AccumuloConfiguration(System.getProperty("accumuloConfig"));
  protected static Connector connect;
  protected static QueryTemplate queryTemplate;
  private static ThreadState threadState;

  static class Connection {
    @Inject
    Connector connect;
  }

  @BeforeSuite
  public static void setup() throws Exception {
    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(AccumuloConfiguration.class).toInstance(config);
        bind(RoleExposingRealm.class).to(FakeRealm.class);
      }}, new AccumuloConnector());

    Connection test = injector.getInstance(Connection.class);
    connect = test.connect;
    queryTemplate = injector.getInstance(QueryTemplate.class);

    String resourceFile = Resources.getResource("version.json").getPath();
    String path = resourceFile.substring(0, resourceFile.lastIndexOf(File.separator));
    File failDir = Files.createTempDir();
    AccumuloImport.importData(connect, OPAL_TABLE, path + "/accumulo_data", failDir.getPath());
    failDir.delete();

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
  public void afterSuite() throws Exception {
    threadState.clear();
  }
}
