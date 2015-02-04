// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import java.util.Map;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

/**
 * Reads value from a single entry in a row.
 *
 * @param <E>
 *     type of result from reading row
 */
public abstract class SingleValueRowMapper<E> implements RowMapper<E> {

  private E value;

  protected void setValue(E value) {
    this.value = value;
  }

  @Override
  public void onBeginRow(Key key) {
  }

  public abstract void onReadEntry(Map.Entry<Key, Value> entry);

  @Override
  public E onEndRow() {
    return value;
  }
}
