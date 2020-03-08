// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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
