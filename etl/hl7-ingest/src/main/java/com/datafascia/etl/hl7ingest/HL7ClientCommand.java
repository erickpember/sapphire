// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7ingest;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.Connection;
import ca.uhn.hl7v2.llp.LLPException;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.parser.PipeParser;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.datafascia.common.command.Command;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

/**
 * Use this command line application to deal with file based HL7 messages. The command copies the
 * files to a running MLLP server instance.
 */
@Parameters(commandNames = "ingest-hl7-files",
    commandDescription = "Copy HL7 message files to MLLP server.")
@Slf4j
public class HL7ClientCommand implements Command {

  @Parameter(names = { "-h", "--host" }, description = "MLLP server host", required = true)
  String mlppHost;

  @Parameter(names = { "-p", "--port" }, description = "MLLP server port", required = true)
  private int port;

  @Parameter(names = "--useTLS", description = "Use TLS on messages. Default is false.",
      required = false)
  private boolean useTLS = false;

  @Parameter(names = "--files", description = "File having HL7 message.", variableArity = true,
      required = true)
  List<String> files;

  @Override
  public int execute() {
    Connection conn = null;
    try {
      HapiContext context = new DefaultHapiContext();
      conn = context.newClient(mlppHost, port, useTLS);
    } catch (HL7Exception e) {
      throw new RuntimeException("Error connecting to MLLP server", e);
    }

    PipeParser pipeParser = new PipeParser();
    for (String file : files) {
      try {
        log.info("Sending file {}", file);
        Message message = pipeParser.parse(content(file));
        Message response = conn.getInitiator().sendAndReceive(message);
        log.info("Response was " + response.encode());
      } catch (LLPException | HL7Exception | IOException e) {
        log.error("Error sending file {} is {}", file, e);
      }
    }
    conn.close();

    return EXIT_STATUS_SUCCESS;
  }

  /**
   * NOTE: it replaces all occurances of LF from the file to a CR as required by HL7 to mark
   * separation between segments.
   *
   * @return contents of file as string
   */
  private String content(String file) throws IOException {
    return new String(readAllBytes(get(file)), StandardCharsets.UTF_8).replace('\n', '\r');
  }
}
