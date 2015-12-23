// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.rest.server.exceptions.InternalErrorException;
import com.datafascia.common.accumulo.AccumuloTemplate;
import com.datafascia.common.avro.schemaregistry.AvroSchemaRegistry;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Stream;
import javax.inject.Inject;
import javax.inject.Named;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.data.Range;
import org.apache.avro.Schema;
import org.apache.hadoop.io.Text;
import org.hl7.fhir.instance.model.api.IBaseResource;

/**
 * Copies values between Java object fields and Accumulo entries.
 */
public class AccumuloFhirEntityStore implements FhirEntityStore {

  private static final Schema DUMMY_SCHEMA = Schema.create(Schema.Type.NULL);
  private static final String DATA = "Data";
  private static final char COMPONENT_SEPARATOR = '=';
  private static final char KEY_SEPARATOR = '&';

  private FhirContext fhirContext;
  private AvroSchemaRegistry schemaRegistry;
  private String dataTableName;
  private Connector connector;
  private AccumuloTemplate accumuloTemplate;

  /**
   * Constructor
   *
   * @param fhirContext
   *     FHIR context
   * @param schemaRegistry
   *     Avro schema registry
   * @param tableNamePrefix
   *     prefix for generating table names
   * @param connector
   *     Accumulo connector
   * @param accumuloTemplate
   *     data access operations template
   */
  @Inject
  public AccumuloFhirEntityStore(
      FhirContext fhirContext,
      AvroSchemaRegistry schemaRegistry,
      @Named("entityTableNamePrefix") String tableNamePrefix,
      Connector connector,
      AccumuloTemplate accumuloTemplate) {

    this.fhirContext = fhirContext;
    this.schemaRegistry = schemaRegistry;
    this.connector = connector;
    this.accumuloTemplate = accumuloTemplate;

    dataTableName = tableNamePrefix + DATA;
    accumuloTemplate.createTableIfNotExist(dataTableName);
  }

  private String getDataTableName() {
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
    if (entityIdElement.getType() == null || entityIdElement.getId() == null) {
      throw new InternalErrorException("Could not generate row ID for "
          + entityIdElement.toString());
    }

    return entityIdElement.getType().getSimpleName() + COMPONENT_SEPARATOR +
        escape(entityIdElement.getId().toString()) + KEY_SEPARATOR;
  }

  /**
   * Converts entity ID to Accumulo row ID.
   *
   * @param entityId
   *     entity ID
   * @return row ID
   */
  public static String toRowId(EntityId entityId) {
    StringBuilder rowId = new StringBuilder();
    for (TypeAndId entityIdElement : entityId.getElements()) {
      rowId.append(toRowId(entityIdElement));
    }

    return rowId.toString();
  }

  @Override
  public void save(EntityId entityId, IBaseResource object) {
    Schema schema = DUMMY_SCHEMA;
    long schemaId = schemaRegistry.putSchema(object.getClass().getSimpleName(), schema);

    accumuloTemplate.save(
        getDataTableName(),
        toRowId(entityId),
        new FhirMutationSetter(fhirContext, schemaId, object));
  }

  @Override
  public <E extends IBaseResource> Optional<E> read(String rowId, Class<E> entityType) {
    Scanner scanner = accumuloTemplate.createScanner(getDataTableName());
    scanner.setRange(Range.exact(rowId));
    scanner.fetchColumnFamily(new Text(entityType.getSimpleName()));

    return accumuloTemplate.queryForObject(scanner, new FhirRowMapper<>(fhirContext, entityType));
  }

  @Override
  public <E extends IBaseResource> Optional<E> read(EntityId entityId) {
    return read(toRowId(entityId), (Class<E>) entityId.getType());
  }

  private static <E> String toRowIdPrefix(EntityId parentId, Class<E> entityType) {
    String prefix = Optional.ofNullable(parentId)
        .map(entityId -> toRowId(entityId))
        .orElse("");
    return prefix + entityType.getSimpleName() + COMPONENT_SEPARATOR;
  }

  @Override
  public <E extends IBaseResource> Stream<E> stream(EntityId parentId, Class<E> entityType) {
    Scanner scanner = accumuloTemplate.createScanner(getDataTableName());
    scanner.setRange(Range.prefix(toRowIdPrefix(parentId, entityType)));
    scanner.fetchColumnFamily(new Text(entityType.getSimpleName()));

    return accumuloTemplate.stream(scanner, new FhirRowMapper<>(fhirContext, entityType));
  }

  @Override
  public <E extends IBaseResource> Stream<E> stream(Class<E> entityType) {
    return stream(null, entityType);
  }

  @Override
  public <E extends IBaseResource> Stream<E> stream(EntityId startEntityId) {
    Class<E> entityType = (Class<E>) startEntityId.getType();

    Scanner scanner = accumuloTemplate.createScanner(getDataTableName());
    scanner.setRange(new Range(toRowId(startEntityId), null));
    scanner.fetchColumnFamily(new Text(entityType.getSimpleName()));

    return accumuloTemplate.stream(scanner, new FhirRowMapper<>(fhirContext, entityType));
  }

  @Override
  public void delete(EntityId entityId) {
    accumuloTemplate.deleteRowIdPrefix(getDataTableName(), toRowId(entityId));
  }

  @Override
  public <E extends IBaseResource> void delete(EntityId parentId, Class<E> entityType) {
    accumuloTemplate.deleteRowIdPrefix(getDataTableName(), toRowIdPrefix(parentId, entityType));
  }

  @Override
  public <E extends IBaseResource> FhirEntityIndex<E> getIndex(
      String indexName, Function<E, String> termSupplier) {

    return new AccumuloFhirEntityIndex<>(indexName, termSupplier, connector, accumuloTemplate);
  }
}
