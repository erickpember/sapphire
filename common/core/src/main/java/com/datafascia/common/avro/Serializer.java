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
package com.datafascia.common.avro;

import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import javax.inject.Inject;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.IndexedRecord;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.reflect.ReflectData;
import org.apache.avro.reflect.ReflectDatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.specific.SpecificRecord;

/**
 * Serializes Avro record to bytes.
 */
public class Serializer {

  private static final byte MAGIC_BYTE = 8;

  private AvroSchemaRegistry schemaRegistry;
  private ConcurrentHashMap<Schema, Long> schemaToIdMap = new ConcurrentHashMap<>();
  private BinaryEncoder encoder;

  /**
   * Constructor
   *
   * @param schemaRegistry
   *     schema registry
   */
  @Inject
  public Serializer(AvroSchemaRegistry schemaRegistry) {
    this.schemaRegistry = schemaRegistry;
  }

  /**
   * Encodes record to bytes.
   *
   * @param topic
   *     topic name
   * @param record
   *     to encode
   * @return bytes
   */
  public byte[] encode(String topic, IndexedRecord record) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    Schema schema = record.getSchema();
    encodeSchemaId(bytes, topic, schema);

    DatumWriter<IndexedRecord> datumWriter;
    if (record instanceof SpecificRecord) {
      datumWriter = new SpecificDatumWriter<>(schema);
    } else {
      datumWriter = new GenericDatumWriter<>(schema);
    }

    encoder = EncoderFactory.get().directBinaryEncoder(bytes, encoder);
    encodePayload(bytes, datumWriter, record);

    return bytes.toByteArray();
  }

  /**
   * Encodes object to bytes.
   *
   * @param topic
   *     topic name
   * @param object
   *     to encode
   * @param <T>
   *     type to encode
   * @return bytes
   */
  public <T> byte[] encodeReflect(String topic, T object) {
    ByteArrayOutputStream bytes = new ByteArrayOutputStream();

    Schema schema = ReflectData.get().getSchema(object.getClass());
    encodeSchemaId(bytes, topic, schema);

    ReflectDatumWriter<T> datumWriter = new ReflectDatumWriter<>(schema);
    encodePayload(bytes, datumWriter, object);

    return bytes.toByteArray();
  }

  private void encodeSchemaId(ByteArrayOutputStream bytes, String topic, Schema schema) {
    bytes.write(MAGIC_BYTE);

    long schemaId = schemaToIdMap.computeIfAbsent(schema, s -> schemaRegistry.putSchema(topic, s));
    try {
      bytes.write(ByteBuffer.allocate(8).putLong(schemaId).array());
    } catch (IOException e) {
      throw new IllegalStateException("encodeSchemaId", e);
    }
  }

  private <T> void encodePayload(
      ByteArrayOutputStream bytes, DatumWriter<T> datumWriter, T record) {

    encoder = EncoderFactory.get().directBinaryEncoder(bytes, encoder);
    try {
      datumWriter.write(record, encoder);
    } catch (IOException e) {
      throw new IllegalStateException("encodePayload", e);
    }
  }
}
