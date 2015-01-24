// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;

/**
 * Bring up mini-cluster as a server and writes its configuraiton to temp directory for other
 * applications to pick up
 */
@Slf4j
public class MiniAccumuloStart {
  /** Accumulo configuration file */
  public static String config = System.getProperty("java.io.tmpdir") +
      File.separatorChar + "accumulo.cfg";
  /** Kill file */
  public static String killFile = System.getProperty("java.io.tmpdir") +
      File.separatorChar + "killAccumulo";

  /** Property name for ZooKeeper instance */
  public static final String ZOOKEEPER = "zookeeper";
  /** Property name for Accumulo instance */
  public static final String INSTANCE = "instance";
  /** Property name for Accumulo user */
  public static final String USER = "user";
  /** Property name for Accumulo password */
  public static final String PASSWORD = "password";
  /** Property name for Accumulo directory */
  public static final String DIRECTORY = "directory";

  /** Default user name */
  public static final String ROOT = "root";
  /** Default user password */
  public static final String USER_PASSWORD = "secret";

  /**
   * Start the server
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    File tempDir = Files.createTempDir();
    MiniAccumuloCluster accumulo = new MiniAccumuloCluster(tempDir, USER_PASSWORD);
    accumulo.start();

    System.out.println("Starting Accumulo mini-cluster ...");
    Thread.sleep(3000);
    exportConfig(accumulo, tempDir);
    System.out.println("  server started ..");

    while (true) {
      Thread.sleep(3000);
      File kill = new File(killFile);
      if (kill.exists()) {
        kill.delete();
        System.out.println("Stopping Accumulo mini-cluster ...");
        accumulo.stop();
      }
    }
  }

  /**
   * Export mini-cluster parameters
   *
   * @param accumulo the Accumulo instance
   * @param tempDir the Accumulo directory
   */
  private static void exportConfig(MiniAccumuloCluster accumulo, File tempDir) throws IOException {
    Properties props = new Properties();
    props.setProperty(INSTANCE, accumulo.getInstanceName());
    props.setProperty(ZOOKEEPER, accumulo.getZooKeepers());
    props.setProperty(USER, ROOT);
    props.setProperty(PASSWORD, USER_PASSWORD);
    props.setProperty(DIRECTORY, tempDir.getAbsolutePath());
    props.store(new FileOutputStream(config), null);
  }
}
