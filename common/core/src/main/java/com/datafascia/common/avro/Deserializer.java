// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.avro;

import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import java.io.IOException;
import java.nio.ByteBuffer;
import javax.inject.Inject;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumReader;
import org.apache.avro.specific.SpecificData;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificRecord;

/**
 * Deserializes bytes to Avro record.
 */
public class Deserializer {

  private static final byte MAGIC_BYTE = 8;

  private AvroSchemaRegistry schemaRegistry;
  private BinaryDecoder decoder;

  /**
   * Constructor
   *
   * @param schemaRegistry
   *     schema registry
   */
  @Inject
  public Deserializer(AvroSchemaRegistry schemaRegistry) {
    this.schemaRegistry = schemaRegistry;
  }

  /**
   * Decodes bytes to record.
   *
   * @param topic
   *     topic name
   * @param bytes
   *     to decode
   * @param recordClass
   *     record class
   * @param <T>
   *     record type
   * @return record
   */
  public <T extends SpecificRecord> T decode(String topic, byte[] bytes, Class<T> recordClass) {
    ByteBuffer buffer = ByteBuffer.wrap(bytes);

    Schema fromSchema = decodeSchemaId(topic, buffer);

    Schema toSchema = SpecificData.get().getSchema(recordClass);
    SpecificDatumReader<T> datumReader = new SpecificDatumReader<>(fromSchema, toSchema);
    return decodePayload(buffer, datumReader);
  }

  /**
   * Decodes bytes to object.
   *
   * @param topic
   *     topic name
   * @param bytes
   *     to decode
   * @param expectedClass
   *     expected class
   * @param <T>
   *     expected type
   * @return record
   */
  public <T> T decodeReflect(String topic, byte[] bytes, Class<T> expectedClass) {
    ByteBuffer buffer = ByteBuffer.wrap(bytes);

    Schema fromSchema = decodeSchemaId(topic, buffer);

    Schema toSchema = ReflectData.get().getSchema(expectedClass);
    ReflectDatumReader<T> datumReader = new ReflectDatumReader<>(fromSchema, toSchema);
    return decodePayload(buffer, datumReader);
  }

  private Schema decodeSchemaId(String topic, ByteBuffer buffer) {
    if (buffer.get() != MAGIC_BYTE) {
      throw new IllegalStateException("Message does not start with magic byte");
    }

    long schemaId = buffer.getLong();
    return schemaRegistry.getSchema(topic, schemaId);
  }

  private <T> T decodePayload(ByteBuffer buffer, DatumReader<T> datumReader) {
    int payloadStart = buffer.position() + buffer.arrayOffset();
    int payloadLength = buffer.limit() - 9;
    decoder = DecoderFactory.get().binaryDecoder(
        buffer.array(), payloadStart, payloadLength, decoder);
    try {
      return datumReader.read(null, decoder);
    } catch (IOException e) {
      throw new IllegalStateException("decodePayload", e);
    }
  }
}
