// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
  String mllpHost;

  @Parameter(names = { "-p", "--port" }, description = "MLLP server port", required = true)
  private int mllpPort;

  @Parameter(names = "--useTLS", description = "Use TLS on messages. Default is false.",
      required = false)
  private boolean useTLS = false;

  @Parameter(names = "--files", description = "File having HL7 message.", variableArity = true,
      required = true)
  List<String> files;

  @Override
  public int execute() {
    HapiContext context = new DefaultHapiContext();
    Connection connection;
    try {
      connection = context.newClient(mllpHost, mllpPort, useTLS);
    } catch (HL7Exception e) {
      throw new IllegalStateException("Error connecting to MLLP server", e);
    }

    PipeParser pipeParser = new PipeParser();
    for (String file : files) {
      try {
        log.info("Sending file {}", file);
        Message message = pipeParser.parse(content(file));
        Message response = connection.getInitiator().sendAndReceive(message);
        log.info("Response was " + response.encode());
      } catch (LLPException | HL7Exception | IOException e) {
        log.error("Error sending file {} is {}", file, e);
      }
    }

    connection.close();
    context.getExecutorService().shutdown();

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
