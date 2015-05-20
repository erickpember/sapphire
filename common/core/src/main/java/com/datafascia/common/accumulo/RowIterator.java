// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

import java.util.Iterator;
import java.util.Map;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;
import org.apache.hadoop.io.Text;

/**
 * Elements returned by this iterator are objects mapped from entries in a row.
 *
 * @param <E>
 *     type of result from mapping entries in a row
 */
public class RowIterator<E> implements Iterator<E> {

  private final Iterator<Map.Entry<Key, Value>> entryIterator;
  private final RowMapper<E> rowMapper;
  private Text rowId;

  /**
   * Constructor
   *
   * @param scanner
   *     iterate entries
   * @param rowMapper
   *     row mapper
   */
  public RowIterator(Iterable<Map.Entry<Key, Value>> scanner, RowMapper<E> rowMapper) {
    this.entryIterator = scanner.iterator();
    this.rowMapper = rowMapper;
  }

  @Override
  public boolean hasNext() {
    return entryIterator.hasNext();
  }

  @Override
  public E next() {
    E result = null;
    while (entryIterator.hasNext() && result == null) {
      Map.Entry<Key, Value> nextEntry = entryIterator.next();
      Text nextRowId = nextEntry.getKey().getRow();
      if (!nextRowId.equals(rowId)) {
        if (rowId != null) {
          result = rowMapper.onEndRow();
        }

        rowId = nextRowId;
        rowMapper.onBeginRow(nextEntry.getKey());
      }

      rowMapper.onReadEntry(nextEntry);
    }

    if (result == null) {
      result = rowMapper.onEndRow();
    }
    return result;
  }
}
