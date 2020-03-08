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
