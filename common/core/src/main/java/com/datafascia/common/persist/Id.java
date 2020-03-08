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
package com.datafascia.common.persist;

import lombok.EqualsAndHashCode;

/**
 * Type-safe primary key ensures entity type of primary key passed to a method
 * parameter matches the expected entity type. The compiler complains with an
 * error if you try to pass a primary key for an entity type different from the
 * expected entity type.
 *
 * @param <E>
 *     type of entity identified by this primary key
 */
@EqualsAndHashCode
public class Id<E> {

  private String id;

  /**
   * Constructs null primary key.
   */
  public Id() {
  }

  /**
   * Constructs primary key from string representation.
   *
   * @param id
   *     string representation
   */
  public Id(String id) {
    this.id = id;
  }

  /**
   * Convenience method constructs primary key from string representation.
   *
   * @param <E>
   *     type of entity identified by this primary key
   * @param key
   *     primary key string
   * @return primary key, or {@code null} if {@code key} is null
   */
  public static <E> Id<E> of(String key) {
    return (key == null) ? null : new Id<>(key);
  }

  /**
   * Converts to string.
   *
   * @return primary key string
   */
  @Override
  public String toString() {
    return id;
  }
}
