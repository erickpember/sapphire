// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dao;

import com.datafascia.accumulo.AccumuloImport;
import com.datafascia.dao.PatientDao;
import com.datafascia.models.Patient;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.security.tokens.PasswordToken;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.client.ZooKeeperInstance;
import org.apache.accumulo.core.security.Authorizations;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;
import org.apache.accumulo.minicluster.MiniAccumuloConfig;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Test for Patient DAO
 */
@Slf4j
public class PatientDaoTest {
  private static final String PASSWORD = "secret";
  private static String OPAL_TABLE = "opal_dF_data";

  private static File accDir;
  private static MiniAccumuloCluster mac;
  private static MiniAccumuloConfig config;
  private static Connector connect;

  @BeforeClass
  public static void setup() throws IOException, InterruptedException, AccumuloException,
      AccumuloSecurityException, TableExistsException, TableNotFoundException {
    accDir = Files.createTempDir();
    config = new MiniAccumuloConfig(accDir, PASSWORD);
    mac = new MiniAccumuloCluster(config);
    log.info("Starting Accumulo mini-cluster.");
    mac.start();

    Thread.sleep(9000);
    connect = new ZooKeeperInstance(mac.getInstanceName(), mac.getZooKeepers()).getConnector("root",
        new PasswordToken(PASSWORD));
    connect.securityOperations().changeUserAuthorizations("root", new Authorizations("System"));

    String resourceFile = Resources.getResource("version.json").getPath();
    String path = resourceFile.substring(0,resourceFile.lastIndexOf(File.separator));
    File failDir = Files.createTempDir();
    AccumuloImport.importData(connect, OPAL_TABLE, path + "/accumulo_data",  failDir.getPath());
    failDir.delete();
  }

  @AfterClass
  public static void tearDown() {
    log.info("Shutting down Accumulo mini-cluster.");
    try {
      mac.stop();
      Thread.sleep(5000);
    } catch (Exception e) {
    }
    accDir.delete();
  }

  /*
   * To bring up the mini-cluster instance and keep it running to use for shell
   */
  @Test(enabled = false)
  public void cluster() throws InterruptedException, IOException {
    Runtime.getRuntime().addShutdownHook(new Thread(() -> { tearDown(); }));
    System.out.println("Instance name: " + mac.getInstanceName());
    System.out.println("ZooKeeper: " + mac.getZooKeepers());
    System.out.println("Path: " + config.getDir().getAbsolutePath());
    System.out.println("hit Ctrl-c to shutdown...");
    System.in.read();
  }

  @Test
  public void patients() throws AccumuloException, AccumuloSecurityException, IOException,
      TableExistsException, TableNotFoundException, InterruptedException {
    log.info("Getting the patients");
    Iterator<Patient> patients = PatientDao.patients(connect);

    int count = 0;
    while (patients.hasNext()) {
      Patient patient = patients.next();
      count++;
    }

    assertEquals(count, 703);
  }
}
