// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.avro.schemaregistry;

import java.util.concurrent.ConcurrentHashMap;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.SchemaNormalization;

/**
 * In-memory schema registory implementation.
 */
@Slf4j
public class MemorySchemaRegistry implements AvroSchemaRegistry {

  private ConcurrentHashMap<Key, Schema> idToSchemaMap = new ConcurrentHashMap<>();

  @Override
  public Long putSchema(String topic, Schema schema) {
    long schemaId = SchemaNormalization.parsingFingerprint64(schema);

    Key key = new Key(topic, schemaId);
    if (idToSchemaMap.put(key, schema) == null) {
      log.debug(
          "Registered schema ID {} in topic [{}] for schema {}",
          new Object[] { schemaId, topic, schema.getName() });
    }

    return schemaId;
  }

  @Override
  public Schema getSchema(String topic, Long schemaId) {
    Key key = new Key(topic, schemaId);
    Schema schema = idToSchemaMap.get(key);
    if (schema == null) {
      throw new IllegalArgumentException(
          String.format("Schema ID [%s] not found for topic [%s]", schemaId, topic));
    }

    return schema;
  }

  @Data
  private static class Key {
    private final String topic;
    private final long schemaId;
  }
}
