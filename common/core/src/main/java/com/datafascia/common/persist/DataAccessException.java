// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist;

/**
 * Root of the hierarchy of data access exceptions
 */
public abstract class DataAccessException extends RuntimeException {

  protected DataAccessException(String message) {
    super(message);
  }

  protected DataAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
