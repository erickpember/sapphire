// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.util.shell.Shell;

/**
 * Shell to run against Accumulo mini-cluster
 */
@Slf4j
public class AccumuloShell {
  /**
   * Start the shell
   *
   * @param args the command line arguments
   *
   * @throws IOException errors reading from configuration file
   */
  public static void main(String[] args) throws IOException {
    Opts opts = new Opts();
    new JCommander(opts, args);

    Properties props = new Properties();
    props.load(new FileInputStream(opts.configFile));
    String[] shellArgs = new String[] {
        "-u",
        props.getProperty(AccumuloConfiguration.USER),
        "-p",
        props.getProperty(AccumuloConfiguration.PASSWORD),
        "-z",
        props.getProperty(AccumuloConfiguration.INSTANCE),
        props.getProperty(AccumuloConfiguration.ZOOKEEPERS)
    };
    Shell.main(shellArgs);
  }

  @Parameters(commandDescription = "Command line parameters to start Accumulo shell.")
  static class Opts {
    @Parameter(names = "-config", description = "File for Accumulo configuration", required = true)
    public String configFile;
  }
}
