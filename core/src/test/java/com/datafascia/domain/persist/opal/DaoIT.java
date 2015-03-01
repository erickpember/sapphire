// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.persist.opal;

import com.datafascia.common.accumulo.AccumuloConfiguration;
import com.datafascia.common.accumulo.AccumuloImport;
import com.datafascia.common.accumulo.AccumuloModule;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.accumulo.AuthorizationsSupplier;
import com.datafascia.common.accumulo.ColumnVisibilityPolicy;
import com.datafascia.common.accumulo.FixedColumnVisibilityPolicy;
import com.datafascia.common.accumulo.SubjectAuthorizationsSupplier;
import com.datafascia.common.shiro.FakeRealm;
import com.datafascia.common.shiro.RoleExposingRealm;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import java.io.File;
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
 * Framework for testing daos.
 */
@Slf4j
public abstract class DaoIT {

  private static final String OPAL_TABLE = "opal_dF_data";

  private static final AccumuloConfiguration config = accumuloConfig();
  protected static Connector connect;
  protected static AccumuloTemplate accumuloTemplate;
  private static ThreadState threadState;

  @BeforeSuite
  public static void setup() throws Exception {
    // Delay to allow time for Accumulo mini-cluster to start.
    TimeUnit.SECONDS.sleep(3);

    Injector injector = Guice.createInjector(
        new AbstractModule() {
          @Override
          protected void configure() {
            bind(AccumuloConfiguration.class).toInstance(config);
            bind(AuthorizationsSupplier.class).to(SubjectAuthorizationsSupplier.class);
            bind(ColumnVisibilityPolicy.class).to(FixedColumnVisibilityPolicy.class);
            bind(RoleExposingRealm.class).to(FakeRealm.class);
          }
        },
        new AccumuloModule());

    connect = injector.getInstance(Connector.class);
    accumuloTemplate = injector.getInstance(AccumuloTemplate.class);

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

  /**
   * Get the configuration associated with the mini-cluster
   */
  private static AccumuloConfiguration accumuloConfig() {
    try {
      // Keep the values here in sync with what is in the pom.xml
      String instanceName = "plugin-it-instance";
      Instance instance = new MiniAccumuloInstance(instanceName,
          new File("target/accumulo-maven-plugin/" + instanceName));
      AccumuloConfiguration accConfig = new AccumuloConfiguration();
      accConfig.setInstance(instanceName);
      accConfig.setZooKeepers(instance.getZooKeepers());
      accConfig.setUser("root");
      accConfig.setPassword("supersecret");

      return accConfig;
    } catch (Exception e) {
      throw new RuntimeException("Error getting mini-cluster configuration", e);
    }
  }
}
