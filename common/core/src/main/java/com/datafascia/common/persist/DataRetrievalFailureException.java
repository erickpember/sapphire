// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist;

/**
 * Thrown if expected data could not be retrieved
 */
public abstract class DataRetrievalFailureException extends NonTransientDataAccessException {

  protected DataRetrievalFailureException(String message) {
    super(message);
  }

  protected DataRetrievalFailureException(String message, Throwable cause) {
    super(message, cause);
  }
}
