// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.datafascia.jcommander.converters.URIConverter;
import java.net.URI;

/**
 * Command line parameters for the Emerge CSV generator
 */
@Parameters(commandDescription = "Generate CSV data for the Emerge application.")
public class EmergeCSVOpts {
  @Parameter(names = "-apiURI", description = "The dataFascia API end-point", required = true,
             converter = URIConverter.class)
  public URI apiEndpoint;

  @Parameter(names = "-csvFile", description = "The name of the CSV file to generate", required = true)
  public String csvFile;

  @Parameter(names = "-user", description = "User name for API access", required = true)
  public String user;

  @Parameter(names = "-password", description = "User password for API access", required = true)
  public String password;
}
