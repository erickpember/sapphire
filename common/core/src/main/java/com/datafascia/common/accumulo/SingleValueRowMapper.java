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

  /**
   * Function is called on an entry being read
   *
   * @param entry the read entry
   */
  @Override
  public abstract void onReadEntry(Map.Entry<Key, Value> entry);

  @Override
  public E onEndRow() {
    return value;
  }
}
