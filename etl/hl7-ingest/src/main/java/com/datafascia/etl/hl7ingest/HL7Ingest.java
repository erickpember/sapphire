// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7ingest;

import com.beust.jcommander.Parameter;
import com.datafascia.common.command.Command;
import com.datafascia.jcommander.converters.URIConverter;
import java.net.URI;

/**
 * Common command line options parameters for dealing with HL7 messages
 */
public abstract class HL7Ingest implements Command {
  @Parameter(names = { "-k", "--kafkaBrokers" }, description = "Kafka brokers", required = true)
  String kafkaBrokers;

  @Parameter(names = { "-q", "--queueName" }, description = "Name of the queue", required = true)
  String queueName;

  @Parameter(names = { "-i", "--institution" }, description = "URN for institution",
      required = true, converter = URIConverter.class)
  URI institution;

  @Parameter(names = { "-f", "--facility" }, description = "URN for facility", required = true,
      converter = URIConverter.class)
  URI facility;

  @Parameter(names = { "-d", "--department" }, description = "URN for department", required = false,
      converter = URIConverter.class)
  URI department = null;

  @Parameter(names = { "-s", "--source" }, description = "URN for source", required = false,
      converter = URIConverter.class)
  URI source = null;

  @Parameter(names = { "-p", "--payloadType" }, description = "URN for payload type",
      required = true, converter = URIConverter.class)
  URI payloadType = null;
}
