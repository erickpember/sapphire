// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

/**
 * Collects rows into a list.
 *
 * @param <E>
 *     element type
 */
public class CollectingRowMapper<E> implements RowMapper<Void> {

  private final RowMapper<E> rowMapper;
  private final List<E> rows = new ArrayList<>();

  /**
   * Construct from mapper
   *
   * @param rowMapper the row mapper
   */
  public CollectingRowMapper(RowMapper<E> rowMapper) {
    this.rowMapper = rowMapper;
  }

  /**
   * @return the list of rows
   */
  public List<E> getRows() {
    return rows;
  }

  @Override
  public void onBeginRow(Key key) {
    rowMapper.onBeginRow(key);
  }

  @Override
  public void onReadEntry(Map.Entry<Key, Value> entry) {
    rowMapper.onReadEntry(entry);
  }

  @Override
  public Void onEndRow() {
    E row = rowMapper.onEndRow();
    if (row != null) {
      rows.add(row);
    }
    return null;
  }
}
