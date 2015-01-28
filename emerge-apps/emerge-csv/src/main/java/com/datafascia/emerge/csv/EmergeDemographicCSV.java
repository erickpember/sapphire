// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.csv;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.datafascia.common.command.Command;
import com.datafascia.jcommander.converters.URIConverter;
import java.io.IOException;
import java.net.URI;
import lombok.extern.slf4j.Slf4j;

/**
 * Command line parameters for the Emerge CSV generator
 */
@Parameters(
    commandNames = "demographic-csv",
    commandDescription = "Generate Demographic CSV data for the Emerge application.")
@Slf4j
public class EmergeDemographicCSV implements Command {
  @Parameter(names = "-apiURI", description = "The dataFascia API endpoint URI", required = true,
             converter = URIConverter.class)
  private URI apiEndpoint;

  @Parameter(names = "-csvFile", description = "The name of the CSV file to generate", required = true)
  private String csvFile;

  @Parameter(names = "-user", description = "User name for API access", required = true)
  private String user;

  @Parameter(names = "-password", description = "User password for API access", required = true)
  private String password;

  @Override
  public int execute() {
    try {
      EmergeDemographicCSVGenerator.generate(apiEndpoint, user, password, csvFile);
    } catch (IOException e) {
      log.error("I/O exception", e);
      return EXIT_STATUS_FAILURE;
    }
    return EXIT_STATUS_SUCCESS;
  }
}
