// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

import com.datafascia.common.persist.IncorrectResultSizeException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

/**
 * Partitions entries by row boundaries.
 *
 * @param <E>
 *     type of result from reading row
 */
public class RowReader<E> {

  private final RowMapper<E> rowMapper;
  private final Predicate<E> filter;
  private final Predicate<E> allMatch;
  private List<E> rows = new ArrayList<>();
  private Text rowId;
  private boolean allMatched;

  /**
   * Constructor
   *
   * @param rowMapper
   *     row mapper
   * @param filter
   *     include only entities satisfying the predicate
   * @param allMatch
   *     short circuit reading scanner by returning false
   */
  public RowReader(RowMapper<E> rowMapper, Predicate<E> filter, Predicate<E> allMatch) {
    this.rowMapper = rowMapper;
    this.filter = filter;
    this.allMatch = allMatch;
  }

  /**
   * Constructor
   *
   * @param rowMapper
   *     row mapper
   * @param filter
   *     include only entities satisfying the predicate
   */
  public RowReader(RowMapper<E> rowMapper, Predicate<E> filter) {
    this(rowMapper, filter, entity -> true);
  }

  /**
   * Constructor
   *
   * @param rowMapper
   *     row mapper
   */
  public RowReader(RowMapper<E> rowMapper) {
    this(rowMapper, entity -> true, entity -> true);
  }

  private void beginRow(Map.Entry<Key, Value> nextEntry) {
    rowId = nextEntry.getKey().getRow();
    rowMapper.onBeginRow(nextEntry.getKey());
  }

  private void endRow() {
    if (rowId == null) {
      return;
    }

    E entity = rowMapper.onEndRow();
    if (filter.test(entity)) {
      rows.add(entity);
      if (!allMatch.test(entity)) {
        allMatched = false;
      }
    }
  }

  private void advanceToNextRow(Map.Entry<Key, Value> nextEntry) {
    endRow();
    beginRow(nextEntry);
  }

  private void next(Map.Entry<Key, Value> nextEntry) {
    Text nextRowId = nextEntry.getKey().getRow();
    if (!nextRowId.equals(rowId)) {
      advanceToNextRow(nextEntry);
    }

    rowMapper.onReadEntry(nextEntry);
  }

  /**
   * Reads list of rows.
   *
   * @param scanner
   *     scanner to read from
   * @return found rows
   */
  public List<E> queryForList(Iterable<Map.Entry<Key, Value>> scanner) {
    Iterator<Map.Entry<Key, Value>> iterator = scanner.iterator();
    allMatched = true;
    while (iterator.hasNext() && allMatched) {
      Map.Entry<Key, Value> entry = iterator.next();
      next(entry);
    }

    endRow();
    return rows;
  }

  /**
   * Reads single expected row.
   *
   * @param scanner
   *     scanner to read from
   * @return optional row, empty if none found
   * @throws IncorrectResultSizeException
   *     if more than one row found
   */
  public Optional<E> queryForObject(Iterable<Map.Entry<Key, Value>> scanner) {
    List<E> rows = queryForList(scanner);

    if (rows.isEmpty()) {
      return Optional.empty();
    }

    if (rows.size() == 1) {
      return Optional.of(rows.get(0));
    }

    throw new IncorrectResultSizeException(
        String.format("Expected 1 row but found %d rows", rows.size()));
  }
}
