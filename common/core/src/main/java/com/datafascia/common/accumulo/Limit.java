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
