// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.Timer;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import org.apache.accumulo.core.client.Connector;
import org.apache.accumulo.core.client.Scanner;
import org.apache.accumulo.core.client.TableNotFoundException;

/**
 * Accumulo data access template methods
 */
@Singleton @Slf4j
public class QueryTemplate {

  private final AuthorizationsProvider authorizationsProvider;
  private final Connector connector;
  private final MetricRegistry metrics;
  private final Map<String, Timer> nameToTimerMap = new HashMap<>();

  /**
   * Construct the query template.
   *
   * @param authorizationsProvider authorizations provider
   * @param connector the Accumulo connector
   * @param metrics the metrics registry
   */
  @Inject
  public QueryTemplate(
      AuthorizationsProvider authorizationsProvider, Connector connector, MetricRegistry metrics) {

    this.authorizationsProvider = authorizationsProvider;
    this.connector = connector;
    this.metrics = metrics;
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
      return connector.createScanner(tableName, authorizationsProvider.get());
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
   * @return entity
   */
  public <E> E queryForObject(Scanner scanner, RowMapper<E> rowMapper) {
    Timer.Context timerContext = getTimerContext("queryForObject", rowMapper);
    try {
      RowReader<E> rowReader = new RowReader<>(rowMapper);
      return rowReader.consume(scanner);
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
   * @param <E>
   *     entity type
   * @return entities
   */
  public <E> List<E> queryForList(Scanner scanner, RowMapper<E> rowMapper) {
    Timer.Context timerContext = getTimerContext("queryForList", rowMapper);
    try {
      CollectingRowMapper<E> collectingRowMapper = new CollectingRowMapper<>(rowMapper);
      RowReader<Void> rowReader = new RowReader<>(collectingRowMapper);
      rowReader.consume(scanner);
      return collectingRowMapper.getRows();
    } finally {
      timerContext.stop();
      scanner.close();
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
