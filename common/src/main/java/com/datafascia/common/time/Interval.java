// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.time;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Range of time in the open interval [startInclusive, endExclusive).  That is, instants must be
 * before the upper bound endpoint.
 *
 * @param <T> instant type
 */
@AllArgsConstructor @Data
public class Interval<T extends Comparable<T>> {
  private T startInclusive;
  private T endExclusive;

  /**
   * Checks if this interval contains the item.
   *
   * @param item
   *     to check for presence in this interval
   * @return {@code true} if the interval contains the item.
   */
  public boolean contains(T item) {
    return item.compareTo(startInclusive) >= 0 && item.compareTo(endExclusive) < 0;
  }
}
