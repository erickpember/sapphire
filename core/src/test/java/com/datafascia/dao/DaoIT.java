// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.datafascia.accumulo.AccumuloConfig;
import com.datafascia.accumulo.AccumuloConnector;
import com.datafascia.accumulo.AccumuloImport;
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
import org.testng.annotations.BeforeSuite;

/**
 * Framework for testing daos.
 */
@Slf4j
public abstract class DaoIT {
  private static String OPAL_TABLE = "opal_dF_data";

  private static AccumuloConfig config = new AccumuloConfig(System.getProperty("accumuloConfig"));
  protected static Connector connect;

  static class Connection {
    @Inject
    Connector connect;
  }

  @BeforeSuite
  public static void setup() throws AccumuloException, AccumuloSecurityException, IOException,
      TableExistsException, TableNotFoundException, InterruptedException {
    Injector injector = Guice.createInjector(new AbstractModule() {
      @Override
      protected void configure() {
        bind(AccumuloConfig.class).toInstance(config);
      }}, new AccumuloConnector());

    Connection test = injector.getInstance(Connection.class);
    connect = test.connect;

    String resourceFile = Resources.getResource("version.json").getPath();
    String path = resourceFile.substring(0, resourceFile.lastIndexOf(File.separator));
    File failDir = Files.createTempDir();
    AccumuloImport.importData(connect, OPAL_TABLE, path + "/accumulo_data", failDir.getPath());
    failDir.delete();
  }
}
