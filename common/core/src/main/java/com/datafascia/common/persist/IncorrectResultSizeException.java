// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
