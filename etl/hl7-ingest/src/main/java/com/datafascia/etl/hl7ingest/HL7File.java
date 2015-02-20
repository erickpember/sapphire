// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7ingest;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.datafascia.domain.model.IngestMessage;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.List;
import java.util.Properties;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;
import lombok.extern.slf4j.Slf4j;

import static java.nio.file.Files.readAllBytes;
import static java.nio.file.Paths.get;

/**
 * Use this command line application to deal with file based HL7 messages
 */
@Parameters(commandNames = "ingest-hl7-files",
    commandDescription = "Copy HL7 message files to ingest queue.")
@Slf4j
public class HL7File extends HL7Ingest {
  @Parameter(names = "--files", description = "File having HL7 message.", variableArity = true,
      required = true)
  List<String> files;

  @Override
  public int execute() {
    Producer<byte[], byte[]> queue = kafkaProducer();
    for (String file : files) {
      try {
        log.info("Sending file {}", file);
        IngestMessage msg = entry(file);
        ObjectMapper mapper = new ObjectMapper();
        KeyedMessage<byte[], byte[]> entry =
            new KeyedMessage<>(queueName, mapper.writeValueAsBytes(msg));
        queue.send(entry);
      } catch (IOException e) {
        log.error("Error sending file {} is {}", file, e);
      }
    }

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

  /**
   * @return contents of file as string
   */
  private String content(String file) throws IOException {
    return new String(readAllBytes(get(file)), StandardCharsets.UTF_8);
  }

  /**
   * @return the queue entry
   */
  private IngestMessage entry(String file) throws IOException {
    IngestMessage msg = new IngestMessage();
    msg.setTimestamp(Instant.now());
    msg.setInstitution(institution);
    msg.setFacility(facility);
    msg.setDepartment(department);
    msg.setSource(source);
    msg.setPayloadType(payloadType);
    msg.setPayload(content(file));

    return msg;
  }
}
