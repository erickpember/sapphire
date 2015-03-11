// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.jackson;

/**
 * Exception for a unit that is not supported.
 */
public class UnsupportedUnitException extends RuntimeException {
  /**
   * Create an UnsupportedUnitException
   * @param message The message to display.
   */
  public UnsupportedUnitException(String message) {
    super(message);
  }
}
