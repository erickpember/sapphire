// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import java.io.File;

/**
 * Command line options parameters for starting and stopping mini-cluster instance of Accumulo
 */
@Parameters(commandDescription = "Command line parameters for mini-cluster Accumulo instance")
public class MiniAccumuloOpts {
  @Parameter(names = "-config", description = "File for Accumulo configuration", required = false)
  public String configFile = System.getProperty("java.io.tmpdir") + File.separatorChar +
      "accumulo.cfg";

  @Parameter(names = "-killFile", description = "Kill file to look for to exit", required = false)
  public String killFile = System.getProperty("java.io.tmpdir") + File.separatorChar +
      "killAccumulo";

  @Parameter(names = "-background", description = "Spawn Accumulo as background", required = false)
  public boolean background = false;
}
