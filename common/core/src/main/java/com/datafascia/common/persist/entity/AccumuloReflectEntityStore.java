// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import javax.inject.Inject;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;

/**
 * Persists Java objects to Accumulo by Avro reflection.
 */
public class AccumuloReflectEntityStore implements ReflectEntityStore {

  private static final String DATA = "Data";
  private static final char COMPONENT_SEPARATOR = '=';
  private static final char KEY_SEPARATOR = '&';

  private final AvroSchemaRegistry schemaRegistry;
  private final String dataTableName;
  private final AccumuloTemplate accumuloTemplate;

  /**
   * Constructor
   *
   * @param schemaRegistry
   *     schema registry
   * @param tableNamePrefix
   *     Accumulo table name prefix
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public AccumuloReflectEntityStore(
      AvroSchemaRegistry schemaRegistry,
      String tableNamePrefix,
      AccumuloTemplate accumuloTemplate) {

    this.schemaRegistry = schemaRegistry;
    this.dataTableName = tableNamePrefix + DATA;
    this.accumuloTemplate = accumuloTemplate;

    accumuloTemplate.createTableIfNotExist(dataTableName);
  }

  public String getDataTableName() {
    return dataTableName;
  }

  private static String escape(String input) {
    try {
      return URLEncoder.encode(input, StandardCharsets.UTF_8.name());
    } catch (UnsupportedEncodingException e) {
      throw new AssertionError("Java must implement UTF-8");
    }
  }

  private static String toRowId(TypeAndId entityIdElement) {
    return entityIdElement.getType().getSimpleName() + COMPONENT_SEPARATOR +
        escape(entityIdElement.getId().toString()) + KEY_SEPARATOR;
  }

  private static String toRowId(EntityId entityId) {
    StringBuilder rowId = new StringBuilder();
    for (TypeAndId entityIdElement : entityId.getElements()) {
      rowId.append(toRowId(entityIdElement));
    }

    return rowId.toString();
  }

  @Override
  public void save(EntityId entityId, Object object) {
    Schema schema = ReflectData.get().getSchema(object.getClass());
    long schemaId = schemaRegistry.putSchema(object.getClass().getSimpleName(), schema);

    accumuloTemplate.save(
        dataTableName, toRowId(entityId), new ReflectMutationSetter(schemaId, object));
  }
}
