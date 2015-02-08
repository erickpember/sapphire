// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
