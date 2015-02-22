// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7ingest;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.domain.model.IngestMessage;
import java.io.IOException;
import java.nio.ByteBuffer;
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

  private MemorySchemaRegistry schemaRegistry = new MemorySchemaRegistry();
  private Serializer serializer = new Serializer(schemaRegistry);

  @Override
  public int execute() {
    Producer<byte[], byte[]> queue = kafkaProducer();
    for (String file : files) {
      try {
        log.info("Sending file {}", file);
        IngestMessage message = entry(file);
        KeyedMessage<byte[], byte[]> entry =
            new KeyedMessage<>(queueName, serializer.encodeReflect(queueName, message));
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
  private ByteBuffer content(String file) throws IOException {
    return ByteBuffer.wrap(readAllBytes(get(file)));
  }

  /**
   * @return the queue entry
   */
  private IngestMessage entry(String file) throws IOException {
    return IngestMessage.builder()
        .timestamp(Instant.now())
        .institution(institution)
        .facility(facility)
        .department(department)
        .source(source)
        .payloadType(payloadType)
        .payload(content(file))
        .build();
  }
}
