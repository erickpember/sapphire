// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7ingest;

import ca.uhn.hl7v2.DefaultHapiContext;
import ca.uhn.hl7v2.HapiContext;
import ca.uhn.hl7v2.app.HL7Service;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.datafascia.common.command.Command;
import com.datafascia.jcommander.converters.URIConverter;
import java.net.URI;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.ProducerConfig;
import lombok.extern.slf4j.Slf4j;

/**
 * Starts an MLLP server to receive HL7 messages and write them to the ingest queue.
 */
@Parameters(commandNames = "ingest-hl7-server",
    commandDescription = "Run HL7 MLLP server to copy HL7 messages to ingest queue.")
@Slf4j
public class HL7ServerCommand implements Command {

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

  @Parameter(names = "--port", description = "MLLP listener port", required = true)
  private int port;

  @Parameter(names = "--useTLS", description = "Use TLS on messages. Default is false.",
      required = false)
  private boolean useTLS = false;

  @Override
  public int execute() {
    log.info("Starting MLLP server on port: {} with TLS: {}", port, useTLS);

    // Create the context under which all HAPI operations will execute.
    HapiContext context = new DefaultHapiContext();
    HL7Service server = context.newServer(port, useTLS);

    // Create handler for all messages
    ReceivingApplication handler = new HL7Receiver(kafkaProducer(), queueName,
        institution, facility, department, source, payloadType);
    server.registerApplication(handler);
    server.start();

    return EXIT_STATUS_SUCCESS;
  }

  /**
   * @return the handle for Kafka
   */
  private Producer<byte[], byte[]> kafkaProducer() {
    Properties properties = new Properties();
    properties.put("metadata.broker.list", kafkaBrokers);
    properties.put("request.required.acks", "1");

    return new Producer<>(new ProducerConfig(properties));
  }
}
