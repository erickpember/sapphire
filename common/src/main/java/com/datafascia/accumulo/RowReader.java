// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import java.util.Map;
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

  private RowMapper<E> rowMapper;
  private Text rowId;

  /**
   * Construct from mapper
   *
   * @param rowMapper the row mapper
   */
  public RowReader(RowMapper<E> rowMapper) {
    this.rowMapper = rowMapper;
  }

  private void beginRow(Map.Entry<Key, Value> nextEntry) {
    rowId = nextEntry.getKey().getRow();
    rowMapper.onBeginRow(nextEntry.getKey());
  }

  private E endRow() {
    return (rowId != null) ? rowMapper.onEndRow() : null;
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
   * Consumes all entries from scanner.
   *
   * @param scanner
   *     scanner to read from
   * @return result from reading last row
   */
  public E consume(Iterable<Map.Entry<Key, Value>> scanner) {
    for (Map.Entry<Key, Value> entry : scanner) {
      next(entry);
    }

    return endRow();
  }
}
