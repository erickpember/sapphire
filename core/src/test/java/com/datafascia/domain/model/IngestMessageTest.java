// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.avro.Deserializer;
import com.datafascia.common.avro.Serializer;
import com.datafascia.common.avro.schemaregistry.MemorySchemaRegistry;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;

/**
 * {@link IngestMessage} serialization test
 */
public class IngestMessageTest {

  private static final String TOPIC = "hl7Message";

  private MemorySchemaRegistry schemaRegistry = new MemorySchemaRegistry();
  private Serializer serializer = new Serializer(schemaRegistry);
  private Deserializer deserializer = new Deserializer(schemaRegistry);

  @Test
  public void should_decode() {
    IngestMessage originalMessage = IngestMessage.builder()
        .timestamp(Instant.now())
        .payloadType(URI.create("urn:df-payloadtype:HL7v2"))
        .payload(ByteBuffer.wrap("MSH".getBytes(StandardCharsets.UTF_8)))
        .build();
    byte[] bytes = serializer.encodeReflect(TOPIC, originalMessage);

    IngestMessage message = deserializer.decodeReflect(TOPIC, bytes, IngestMessage.class);
    assertEquals(message.getTimestamp(), originalMessage.getTimestamp());
    assertEquals(message.getPayloadType(), originalMessage.getPayloadType());
    assertEquals(message.getPayload(), originalMessage.getPayload());
    assertNull(message.getInstitution());
  }
}
