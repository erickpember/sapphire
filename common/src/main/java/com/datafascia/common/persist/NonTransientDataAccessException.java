// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist;

/**
 * Root of the hierarchy of data access exceptions where retrying the same
 * operation would fail unless the cause of the exception is corrected.
 */
public abstract class NonTransientDataAccessException extends DataAccessException {

  protected NonTransientDataAccessException(String message) {
    super(message);
  }

  protected NonTransientDataAccessException(String message, Throwable cause) {
    super(message, cause);
  }
}
