// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

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
   */
  public static void main(String[] args) throws IOException {
    System.out.println("Stopping the Accumulo mini-cluster ... ");
    File kill = new File(MiniAccumuloStart.killFile);
    kill.createNewFile();
  }
}
