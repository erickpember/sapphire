// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
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

  private RowMapper<E> rowMapper;
  private List<E> rows = new ArrayList<>();

  public CollectingRowMapper(RowMapper<E> rowMapper) {
    this.rowMapper = rowMapper;
  }

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
