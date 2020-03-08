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
package com.datafascia.common.persist.entity;

import com.datafascia.common.accumulo.RowMapper;
import com.datafascia.common.jackson.DFObjectMapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Splitter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

/**
 * Copies values from Accumulo row to Java object.
 *
 * @param <T>
 *     entity type
 */
public class ReflectRowMapper<T> implements RowMapper<T> {

  private static final ObjectMapper OBJECT_MAPPER = DFObjectMapper.objectMapper();
  private static final Splitter DOT_SPLITTER = Splitter.on('.');

  private final Class<T> entityType;
  private ObjectNode rootObjectNode;
  private long lastTimestamp;

  /**
   * Constructor
   *
   * @param entityType
   *     entity type
   */
  public ReflectRowMapper(Class<T> entityType) {
    this.entityType = entityType;
  }

  @Override
  public void onBeginRow(Key key) {
    rootObjectNode = OBJECT_MAPPER.createObjectNode();
  }

  private ObjectNode followPath(List<String> fieldNames) {
    ObjectNode objectNode = rootObjectNode;
    for (String fieldName : fieldNames) {
      objectNode = objectNode.with(fieldName);
    }

    return objectNode;
  }

  private JsonNode readJson(byte[] bytes) {
    try {
      return OBJECT_MAPPER.readTree(bytes);
    } catch (IOException e) {
      String json = new String(bytes, StandardCharsets.UTF_8);
      throw new IllegalStateException("Cannot read JSON " + json, e);
    }
  }

  @Override
  public void onReadEntry(Map.Entry<Key, Value> entry) {
    String fieldNamePath = entry.getKey().getColumnQualifier().toString();
    if (ReflectMutationSetter.SCHEMA_ID.equals(fieldNamePath)) {
      lastTimestamp = entry.getKey().getTimestamp();
      return;
    }

    if (entry.getKey().getTimestamp() < lastTimestamp) {
      // Ignore old version entry.
      return;
    }

    List<String> fieldNames = DOT_SPLITTER.splitToList(fieldNamePath);
    String fieldName = fieldNames.get(fieldNames.size() - 1);

    ObjectNode objectNode = followPath(fieldNames.subList(0, fieldNames.size() - 1));
    objectNode.set(fieldName, readJson(entry.getValue().get()));
  }

  @Override
  public T onEndRow() {
    try {
      return OBJECT_MAPPER.treeToValue(rootObjectNode, entityType);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Cannot convert JSON to type " + entityType, e);
    }
  }
}
