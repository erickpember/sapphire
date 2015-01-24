// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import java.io.FileInputStream;
import com.datafascia.accumulo.MiniAccumuloStart;
import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.util.shell.Shell;

/**
 * Shell to run against Accumulo mini-cluster
 */
@Slf4j
public class AccumuloShell {
  private static Properties props = new Properties();

  /**
   * Start the server
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) throws IOException {
    props.load(new FileInputStream(MiniAccumuloStart.config));
    String instance = props.getProperty(MiniAccumuloStart.INSTANCE);
    String zookeeper = props.getProperty(MiniAccumuloStart.ZOOKEEPER);
    String user = props.getProperty(MiniAccumuloStart.USER);
    String password = props.getProperty(MiniAccumuloStart.PASSWORD);

    String[] shellArgs = new String[]{"-u", user, "-p", password, "-z", instance, zookeeper};
    Shell.main(shellArgs);
  }
}
