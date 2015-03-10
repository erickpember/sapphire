// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

import java.util.function.Predicate;

/**
 * Given a count, returns true the first count times this predicate is invoked,
 * then returns false on subsequent invocations.
 *
 * @param <E>
 *     entity type
 */
public class Limit<E> implements Predicate<E> {

  private final int trueCount;
  private int count = 0;

  /**
   * Constructor
   *
   * @param trueCount
   *    number of invocations returning true
   */
  public Limit(int trueCount) {
    this.trueCount = trueCount;
  }

  @Override
  public boolean test(E entity) {
    ++count;
    return count <= trueCount;
  }
}
