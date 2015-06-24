// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist.entity;

import ca.uhn.fhir.context.FhirContext;
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
import org.apache.hadoop.io.Text;
import org.hl7.fhir.instance.model.IBaseResource;

/**
 * Copies values between Java object fields and Accumulo entries.
 */
public class AccumuloFhirEntityStore implements FhirEntityStore {

  private static final Schema DUMMY_SCHEMA = Schema.create(Schema.Type.NULL);
  private static final String DATA = "Data";
  private static final char COMPONENT_SEPARATOR = '=';
  private static final char KEY_SEPARATOR = '&';

  @Inject
  private FhirContext fhirContext;

  @Inject
  private AvroSchemaRegistry schemaRegistry;

  @Inject @Named("entityTableNamePrefix")
  private String tableNamePrefix;

  @Inject
  private AccumuloTemplate accumuloTemplate;

  private String getDataTableName() {
    String dataTableName = tableNamePrefix + DATA;
    accumuloTemplate.createTableIfNotExist(dataTableName);
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
  public void save(EntityId entityId, IBaseResource object) {
    Schema schema = DUMMY_SCHEMA;
    long schemaId = schemaRegistry.putSchema(object.getClass().getSimpleName(), schema);

    accumuloTemplate.save(
        getDataTableName(),
        toRowId(entityId),
        new FhirMutationSetter(fhirContext, schemaId, object));
  }

  @Override
  public <E extends IBaseResource> Optional<E> read(EntityId entityId) {
    Class<E> entityType = (Class<E>) entityId.getType();

    Scanner scanner = accumuloTemplate.createScanner(getDataTableName());
    scanner.setRange(Range.exact(toRowId(entityId)));
    scanner.fetchColumnFamily(new Text(entityType.getSimpleName()));

    return accumuloTemplate.queryForObject(scanner, new FhirRowMapper<>(fhirContext, entityType));
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
  public <E extends IBaseResource> void delete(EntityId parentId, Class<E> entityType) {
    accumuloTemplate.delete(getDataTableName(), toRowIdPrefix(parentId, entityType));
  }
}
