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

  public static final String INSTANT_INTERVAL_SCHEMA =
      "{" +
        "\"type\": \"record\", " +
        "\"name\": \"InstantInterval\", " +
        "\"fields\": [" +
          "{ \"name\": \"start\", \"type\": \"string\" }," +
          "{ \"name\": \"end\", \"type\": \"string\" }" +
        "]" +
      "}";

  @JsonProperty("start")
  private T startInclusive;

  @JsonProperty("end")
  private T endExclusive;

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
