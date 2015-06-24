// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import ca.uhn.fhir.context.FhirContext;
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
import org.hl7.fhir.instance.model.IBaseResource;

/**
 * Copies values from Accumulo row to Java object.
 *
 * @param <T>
 *     entity type
 */
public class FhirRowMapper<T extends IBaseResource> implements RowMapper<T> {

  private static final ObjectMapper OBJECT_MAPPER = DFObjectMapper.objectMapper();
  private static final Splitter DOT_SPLITTER = Splitter.on('.');

  private final FhirContext fhirContext;
  private final Class<T> entityType;
  private ObjectNode rootObjectNode;

  /**
   * Constructor
   *
   * @param fhirContext
   *     FHIR context
   * @param entityType
   *     entity type
   */
  public FhirRowMapper(FhirContext fhirContext, Class<T> entityType) {
    this.fhirContext = fhirContext;
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
      // Ignore entry containing schema ID.
      return;
    }

    List<String> fieldNames = DOT_SPLITTER.splitToList(fieldNamePath);
    String fieldName = fieldNames.get(fieldNames.size() - 1);

    ObjectNode objectNode = followPath(fieldNames.subList(0, fieldNames.size() - 1));
    objectNode.set(fieldName, readJson(entry.getValue().get()));
  }

  @Override
  public T onEndRow() {
    String json;
    try {
      json = OBJECT_MAPPER.writeValueAsString(rootObjectNode);
    } catch (JsonProcessingException e) {
      throw new IllegalStateException("Cannot convert to JSON", e);
    }

    return fhirContext.newJsonParser().parseResource(entityType, json);
  }
}
