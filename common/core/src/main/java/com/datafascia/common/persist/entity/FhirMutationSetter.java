// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import ca.uhn.fhir.context.FhirContext;
import com.datafascia.common.accumulo.MutationBuilder;
import com.datafascia.common.accumulo.MutationSetter;
import com.datafascia.common.jackson.DFObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import org.apache.accumulo.core.data.Value;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * Copies values from JSON object to Accumulo mutation.
 */
public class FhirMutationSetter implements MutationSetter {

  static final String SCHEMA_ID = "$schemaId";

  private static final ObjectMapper OBJECT_MAPPER = DFObjectMapper.objectMapper();
  private final String typeName;
  private final long schemaId;
  private final JsonNode objectNode;
  private MutationBuilder mutationBuilder;

  /**
   * Constructor
   *
   * @param fhirContext
   *     FHIR context
   * @param schemaId
   *     schema ID
   * @param object
   *     to save
   */
  public FhirMutationSetter(FhirContext fhirContext, long schemaId, IBaseResource object) {
    this.typeName = object.getClass().getSimpleName();
    this.schemaId = schemaId;

    String json = fhirContext.newJsonParser().encodeResourceToString(object);
    try {
      this.objectNode = OBJECT_MAPPER.readTree(json);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot parse json " + json, e);
    }
  }

  private byte[] toJson(Object value) {
    try {
      return OBJECT_MAPPER.writeValueAsBytes(value);
    } catch (IOException e) {
      throw new IllegalStateException("Cannot encode to JSON", e);
    }
  }

  private void writeObject(JsonNode objectNode, String parentFieldPath) {
    Iterator<Map.Entry<String, JsonNode>> iEntry = objectNode.fields();
    while (iEntry.hasNext()) {
      Map.Entry<String, JsonNode> entry = iEntry.next();
      String fieldName = entry.getKey();
      JsonNode value = entry.getValue();
      if (value.isObject()) {
        writeObject(value, parentFieldPath + fieldName + '.');
      } else {
        String columnQualifier = parentFieldPath + fieldName;
        mutationBuilder.put(columnQualifier, new Value(toJson(value)));
      }
    }
  }

  @Override
  public void putWriteOperations(MutationBuilder mutationBuilder) {
    this.mutationBuilder = mutationBuilder;

    mutationBuilder.columnFamily(typeName);

    mutationBuilder.put(SCHEMA_ID, schemaId);

    writeObject(objectNode, "");
  }
}
