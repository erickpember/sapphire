// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.time;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Range of time in the open interval [startInclusive, endExclusive). That is,
 * items in the interval must be less than the end boundary.
 *
 * @param <T> instant type
 */
@AllArgsConstructor @NoArgsConstructor @Data
public class Interval<T extends Comparable<T>> {
  @JsonProperty("start")
  protected T startInclusive;
  @JsonProperty("end")
  protected T endExclusive;

  /**
   * Checks if this interval contains the item.
   *
   * @param item
   *     to check for presence in this interval
   * @return {@code true} if the interval contains the item.
   */
  public boolean contains(T item) {
    if (startInclusive == null || endExclusive == null) {
      return false;
    }
    return item.compareTo(startInclusive) >= 0 && item.compareTo(endExclusive) < 0;
  }
}
