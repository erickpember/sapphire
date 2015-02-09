// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.beust.jcommander.JCommander;
import java.io.File;
import java.io.IOException;
import lombok.extern.slf4j.Slf4j;

/**
 * Stop the Accumulo mini-cluster by creating the kill file it is looking for
 */
@Slf4j
public class MiniAccumuloStop {
  /**
   * Stop the server
   *
   * @param args the command line arguments
   *
   * @throws InterruptedException for thread process
   * @throws IOException should never be thrown. Needed for compilation check
   */
  public static void main(String[] args) throws IOException, InterruptedException {
    MiniAccumuloOpts opts = new MiniAccumuloOpts();
    new JCommander(opts, args);

    new File(opts.killFile).createNewFile();
    System.out.print("Stopping the Accumulo mini-cluster ... ");
    while (true) {
      System.out.print(".");
      Thread.sleep(3000);
      if (!new File(opts.killFile).exists()) {
        return;
      }
    }
  }
}
