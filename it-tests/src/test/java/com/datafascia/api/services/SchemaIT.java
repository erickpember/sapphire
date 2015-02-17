// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.fasterxml.jackson.module.jsonSchema.JsonSchema;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertTrue;

/**
 * Integration tests for schema resources
 */
@Slf4j
public class SchemaIT extends ApiIT {
  /**
   * Validate that schemas are returned.
   */
  @Test
  public static void testSchemaPull() {
    List<JsonSchema> schemas = api.schemas();
    assertTrue(schemas.size() > 0);

    List<String> models = Arrays.asList(new String[] {"Patient", "Encounter", "Observation"});
    for (String model : models) {
      api.schema(model);
    }
  }
}
