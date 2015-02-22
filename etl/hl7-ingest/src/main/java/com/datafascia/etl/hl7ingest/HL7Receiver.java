// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7ingest;

import ca.uhn.hl7v2.HL7Exception;
import ca.uhn.hl7v2.model.Message;
import ca.uhn.hl7v2.protocol.ReceivingApplication;
import ca.uhn.hl7v2.protocol.ReceivingApplicationException;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import com.datafascia.domain.model.IngestMessage;
import java.io.IOException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Map;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles all HL7 messages and writes to queue
 */
@Slf4j @RequiredArgsConstructor
public class HL7Receiver implements ReceivingApplication {
  private final Producer<byte[], byte[]> queue;
  private final String queueName;
  private final URI institution;
  private final URI facility;
  private final URI department;
  private final URI source;
  private final URI payloadType;

  private MemorySchemaRegistry schemaRegistry = new MemorySchemaRegistry();
  private Serializer serializer = new Serializer(schemaRegistry);

  @Override
  public boolean canProcess(Message message) {
    return true;
  }

  @Override
  public Message processMessage(Message hl7, Map<String, Object> metadata)
      throws ReceivingApplicationException, HL7Exception {

    IngestMessage message = IngestMessage.builder()
        .timestamp(Instant.now())
        .institution(institution)
        .facility(facility)
        .department(department)
        .source(source)
        .payloadType(payloadType)
        .payload(ByteBuffer.wrap(hl7.encode().getBytes(StandardCharsets.UTF_8)))
        .build();

    try {
      KeyedMessage<byte[], byte[]> entry =
          new KeyedMessage<>(queueName, serializer.encodeReflect(queueName, message));
      queue.send(entry);

      return hl7.generateACK();
    } catch (IOException e) {
      throw new HL7Exception(e);
    }
  }
}
