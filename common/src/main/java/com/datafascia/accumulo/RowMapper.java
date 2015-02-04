// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import java.util.Map;
import org.apache.accumulo.core.data.Key;
import org.apache.accumulo.core.data.Value;

/**
 * Callback methods for receiving entries in a row.
 *
 * @param <E>
 *     type of result from reading row
 */
public interface RowMapper<E> {

  /**
   * Called before first entry in row.
   *
   * @param key
   *     row key
   */
  void onBeginRow(Key key);

  /**
   * Called for each entry.
   *
   * @param entry
   *     entry to read
   */
  void onReadEntry(Map.Entry<Key, Value> entry);

  /**
   * Called after last entry in row.
   *
   * @return result from reading row
   */
  E onEndRow();
}
