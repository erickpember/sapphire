// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.AccumuloException;
import org.apache.accumulo.core.client.AccumuloSecurityException;
import org.apache.accumulo.core.client.BatchDeleter;
import org.apache.accumulo.core.client.BatchWriter;
import org.apache.accumulo.core.client.BatchWriterConfig;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.MutationsRejectedException;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableExistsException;
import org.apache.accumulo.core.client.TableNotFoundException;
import org.apache.accumulo.core.data.Range;

/**
 * Accumulo data access template methods
 */
@Singleton @Slf4j
public class AccumuloTemplate {

  private final Connector connector;
  private final ColumnVisibilityPolicy columnVisibilityPolicy;
  private final AuthorizationsSupplier authorizationsSupplier;
  private final MetricRegistry metrics;
  private final Map<String, Timer> nameToTimerMap = new HashMap<>();

  /**
   * Construct the query template.
   *
   * @param connector
   *     the Accumulo connector
   * @param columnVisibilityPolicy
   *     supplies visibility expression to write when writing an entry
   * @param authorizationsSupplier
   *     supplies authorizations for reading entries
   * @param metrics
   *     the metrics registry
   */
  @Inject
  public AccumuloTemplate(
      Connector connector,
      ColumnVisibilityPolicy columnVisibilityPolicy,
      AuthorizationsSupplier authorizationsSupplier,
      MetricRegistry metrics) {

    this.connector = connector;
    this.columnVisibilityPolicy = columnVisibilityPolicy;
    this.authorizationsSupplier = authorizationsSupplier;
    this.metrics = metrics;
  }

  /**
   * Creates table if it does not exist.
   *
   * @param tableName
   *     table name
   */
  public void createTableIfNotExist(String tableName) {
    if (connector.tableOperations().exists(tableName)) {
      return;
    }

    try {
      connector.tableOperations().create(tableName);
    } catch (AccumuloException | AccumuloSecurityException | TableExistsException e) {
      throw new IllegalStateException("Cannot create table " + tableName, e);
    }
  }

  private BatchWriter createBatchWriter(String tableName) {
    try {
      return connector.createBatchWriter(tableName, new BatchWriterConfig());
    } catch (TableNotFoundException e) {
      throw new IllegalStateException("Table " + tableName + " not found", e);
    }
  }

  /**
   * Writes entries to a row.
   *
   * @param tableName
   *     table name
   * @param rowId
   *     row ID
   * @param mutationSetter
   *     puts write operations in a mutation
   */
  public void save(String tableName, String rowId, MutationSetter mutationSetter) {
    BatchWriter writer = createBatchWriter(tableName);
    MutationBuilder mutationBuilder = new MutationBuilder(tableName, rowId, columnVisibilityPolicy);
    mutationSetter.putWriteOperations(mutationBuilder);
    try {
      writer.addMutation(mutationBuilder.build());
      writer.close();
    } catch (MutationsRejectedException e) {
      throw new IllegalStateException("Cannot save row ID " + rowId, e);
    }
  }

  /**
   * Creates a scanner of the table.
   *
   * @param tableName
   *     table to scan
   * @return scanner
   * @throws RuntimeException
   *     if table not found
   */
  public Scanner createScanner(String tableName) {
    try {
      return connector.createScanner(tableName, authorizationsSupplier.get());
    } catch (TableNotFoundException e) {
      throw new IllegalStateException("Table " + tableName + " not found", e);
    }
  }

  /**
   * Reads entries from row into an object. Also closes scanner.
   *
   * @param scanner
   *     scanner to read from
   * @param rowMapper
   *     callback to receive entries
   * @param <E>
   *     entity type
   * @return optional entity, empty if none found
   * @throws com.datafascia.common.persist.IncorrectResultSizeException
   *     if more than one row found
   */
  public <E> Optional<E> queryForObject(Scanner scanner, RowMapper<E> rowMapper) {
    Timer.Context timerContext = getTimerContext("queryForObject", rowMapper);
    try {
      RowReader<E> rowReader = new RowReader<>(rowMapper);
      return rowReader.queryForObject(scanner);
    } finally {
      timerContext.stop();
      scanner.close();
    }
  }

  /**
   * Reads entries from rows into a list of objects. Also closes scanner.
   *
   * @param scanner
   *     scanner to read from
   * @param rowMapper
   *     callback to receive entries
   * @param filter
   *     include only entities satisfying the predicate
   * @param allMatch
   *     short circuit reading scanner by returning false
   * @param <E>
   *     entity type
   * @return entities
   */
  public <E> List<E> queryForList(
      Scanner scanner, RowMapper<E> rowMapper, Predicate<E> filter, Predicate<E> allMatch) {

    Timer.Context timerContext = getTimerContext("queryForList", rowMapper);
    try {
      RowReader<E> rowReader = new RowReader<>(rowMapper, filter, allMatch);
      return rowReader.queryForList(scanner);
    } finally {
      timerContext.stop();
      scanner.close();
    }
  }

  /**
   * Reads entries from rows into a list of objects. Also closes scanner.
   *
   * @param scanner
   *     scanner to read from
   * @param rowMapper
   *     callback to receive entries
   * @param filter
   *     include only entities satisfying the predicate
   * @param <E>
   *     entity type
   * @return entities
   */
  public <E> List<E> queryForList(Scanner scanner, RowMapper<E> rowMapper, Predicate<E> filter) {
    return queryForList(scanner, rowMapper, filter, entity -> true);
  }

  /**
   * Reads entries from rows into a list of objects. Also closes scanner.
   *
   * @param scanner
   *     scanner to read from
   * @param rowMapper
   *     callback to receive entries
   * @param <E>
   *     entity type
   * @return entities
   */
  public <E> List<E> queryForList(Scanner scanner, RowMapper<E> rowMapper) {
    return queryForList(scanner, rowMapper, entity -> true);
  }

  /**
   * Deletes all entries for an entity.
   *
   * @param tableName
   *     table name
   * @param rowIdPrefix
   *     row ID prefix
   */
  public void delete(String tableName, String rowIdPrefix) {
    BatchDeleter deleter = null;
    try {
      deleter = connector.createBatchDeleter(
          tableName, authorizationsSupplier.get(), 1, new BatchWriterConfig());
      deleter.setRanges(Arrays.asList(Range.prefix(rowIdPrefix)));
      deleter.delete();
    } catch (MutationsRejectedException e) {
      throw new IllegalStateException("Cannot delete row ID prefix " + rowIdPrefix, e);
    } catch (TableNotFoundException e) {
      throw new IllegalStateException("Table " + tableName + " not found", e);
    } finally {
      if (deleter != null) {
        deleter.close();
      }
    }
  }

  private <E> Timer.Context getTimerContext(String methodName, RowMapper<E> rowMapper) {
    return getTimerContext(getClass(), methodName, rowMapper.getClass().getName());
  }

  /**
   * Gets context for a timer for the given name.
   *
   * @param clazz
   *     first element of the name
   * @param names
   *     remaining elements of the name
   * @return context
   */
  public Timer.Context getTimerContext(Class<?> clazz, String... names) {
    String key = MetricRegistry.name(clazz, names);
    Timer timer = nameToTimerMap.computeIfAbsent(key, k -> metrics.timer(k));
    return timer.time();
  }
}
