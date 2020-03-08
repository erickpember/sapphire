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

/**
 * Thrown if a result is not of the expected size, for example when expecting
 * a single row but getting 0 or more than 1 row.
 */
public class IncorrectResultSizeException extends DataRetrievalFailureException {

  /**
   * Constructor
   *
   * @param message
   *     detail message
   */
  public IncorrectResultSizeException(String message) {
    super(message);
  }

  /**
   * Constructor
   *
   * @param message
   *     detail message
   * @param cause
   *     root cause
   */
  public IncorrectResultSizeException(String message, Throwable cause) {
    super(message, cause);
  }
}
