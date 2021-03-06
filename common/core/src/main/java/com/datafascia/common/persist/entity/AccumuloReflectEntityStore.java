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

import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Range;
import org.apache.avro.Schema;
import org.apache.avro.reflect.ReflectData;
import org.apache.hadoop.io.Text;

/**
 * Copies values between Java object fields and Accumulo entries by reflection.
 */
public class AccumuloReflectEntityStore implements ReflectEntityStore {

  private static final String DATA = "Data";
  private static final char COMPONENT_SEPARATOR = '=';
  private static final char KEY_SEPARATOR = '&';

  private AvroSchemaRegistry schemaRegistry;
  private String dataTableName;
  private AccumuloTemplate accumuloTemplate;

  /**
   * Constructor
   *
   * @param schemaRegistry
   *     Avro schema registry
   * @param tableNamePrefix
   *     prefix for generating table names
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public AccumuloReflectEntityStore(
      AvroSchemaRegistry schemaRegistry,
      @Named("entityTableNamePrefix") String tableNamePrefix,
      AccumuloTemplate accumuloTemplate) {

    this.schemaRegistry = schemaRegistry;
    this.accumuloTemplate = accumuloTemplate;

    dataTableName = tableNamePrefix + DATA;
    accumuloTemplate.createTableIfNotExist(dataTableName);
  }

  /**
   * Gets name of Accumulo table storing entity data.
   *
   * @return Accumulo table name
   */
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
        getDataTableName(),
        toRowId(entityId),
        new ReflectMutationSetter(schemaId, object));
  }

  @Override
  public <E> Optional<E> read(EntityId entityId) {
    Class<E> entityType = (Class<E>) entityId.getType();

    Scanner scanner = accumuloTemplate.createScanner(getDataTableName());
    scanner.setRange(Range.exact(toRowId(entityId)));
    scanner.fetchColumnFamily(new Text(entityType.getSimpleName()));

    return accumuloTemplate.queryForObject(scanner, new ReflectRowMapper<>(entityType));
  }

  private static <E> String toRowIdPrefix(EntityId parentId, Class<E> entityType) {
    String prefix = Optional.ofNullable(parentId)
        .map(entityId -> toRowId(entityId))
        .orElse("");
    return prefix + entityType.getSimpleName() + COMPONENT_SEPARATOR;
  }

  @Override
  public <E> Stream<E> stream(EntityId parentId, Class<E> entityType) {
    Scanner scanner = accumuloTemplate.createScanner(getDataTableName());
    scanner.setRange(Range.prefix(toRowIdPrefix(parentId, entityType)));
    scanner.fetchColumnFamily(new Text(entityType.getSimpleName()));

    return accumuloTemplate.stream(scanner, new ReflectRowMapper<>(entityType));
  }

  @Override
  public <E> Stream<E> stream(Class<E> entityType) {
    return stream(null, entityType);
  }

  @Override
  public <E> Stream<E> stream(EntityId startEntityId) {
    Class<E> entityType = (Class<E>) startEntityId.getType();

    Scanner scanner = accumuloTemplate.createScanner(getDataTableName());
    scanner.setRange(new Range(toRowId(startEntityId), null));
    scanner.fetchColumnFamily(new Text(entityType.getSimpleName()));

    return accumuloTemplate.stream(scanner, new ReflectRowMapper<>(entityType));
  }

  @Override
  public void delete(EntityId entityId) {
    accumuloTemplate.deleteRowIdPrefix(getDataTableName(), toRowId(entityId));
  }

  @Override
  public <E> void delete(EntityId parentId, Class<E> entityType) {
    accumuloTemplate.deleteRowIdPrefix(getDataTableName(), toRowIdPrefix(parentId, entityType));
  }
}
