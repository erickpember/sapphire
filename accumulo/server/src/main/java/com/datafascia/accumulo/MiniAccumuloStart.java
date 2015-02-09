// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.beust.jcommander.JCommander;
import com.datafascia.accumulo.AccumuloConfig;
import com.google.common.io.Files;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.minicluster.MiniAccumuloCluster;

/**
 * Bring up mini-cluster as a server and writes its configuraiton to temp directory for other
 * applications to pick up
 */
@Slf4j
public class MiniAccumuloStart {
  private static MiniAccumuloOpts opts = new MiniAccumuloOpts();

  /**
   * Start the server
   *
   * @param args the command line arguments
   *
   * @throws InterruptedException for thread process
   * @throws IOException should never be thrown. Needed for compilation check
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    new JCommander(opts, args);

    System.out.println("Starting Accumulo mini-cluster ...");
    setClasspath();

    Runnable run = runnable();
    if (opts.background) {
      new Thread(run){{ setDaemon(true); }}.start();
      Thread.sleep(6000);
    } else {
      run.run();
      System.in.read();
    }
  }

  /**
   * @return the function to execute
   */
  private static Runnable runnable() {
    return (() -> {
      try {
        File tmpDir = Files.createTempDir();
        MiniAccumuloCluster accInst = new MiniAccumuloCluster(tmpDir, AccumuloConfig.USER_PASSWORD);
        accInst.start();
        exportConfig(accInst, tmpDir);
        System.out.println("  server started ..");

        while (true) {
          Thread.sleep(2000);
          File kill = new File(opts.killFile);
          if (kill.exists()) {
            kill.delete();
            System.out.println("Kill file detected ...");
            accInst.stop();
          }
        }
      } catch (IOException | InterruptedException e) {
        new RuntimeException(e);
      }
    });
  }

  /**
   * Set up classpath for next executable
   */
  private static void setClasspath() {
    String sep = "";
    StringBuffer buffer = new StringBuffer();
    for (URL url : ((URLClassLoader) (Thread.currentThread() .getContextClassLoader())).getURLs()) {
      buffer.append(sep);
      buffer.append(new File(url.getPath()));
      sep = System.getProperty("path.separator");
    }
    System.setProperty("java.class.path", buffer.toString());
  }

  /**
   * Export mini-cluster parameters
   *
   * @param accumulo the Accumulo instance
   * @param tempDir the Accumulo directory
   */
  private static void exportConfig(MiniAccumuloCluster accumulo, File tempDir) throws IOException {
    Properties props = new Properties();
    props.setProperty(AccumuloConfig.INSTANCE, accumulo.getInstanceName());
    props.setProperty(AccumuloConfig.ZOOKEEPERS, accumulo.getZooKeepers());
    props.setProperty(AccumuloConfig.USER, AccumuloConfig.ROOT);
    props.setProperty(AccumuloConfig.PASSWORD, AccumuloConfig.USER_PASSWORD);
    props.setProperty(AccumuloConfig.DIRECTORY, tempDir.getAbsolutePath());
    props.setProperty(AccumuloConfig.TYPE, AccumuloConfig.MINI);
    props.store(new FileOutputStream(opts.configFile), null);
  }
}
