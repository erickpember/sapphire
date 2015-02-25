// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.persist;

/**
 * Provides a code representing an enum constant in a data store or message
 * protocol. Storing a code instead of the enum constant name allows us to
 * rename the enum constant without having to worry about migrating values
 * previously stored with the old name.
 *
 * @param <C>
 *     code representation type
 */
public interface Code<C> {

  /**
   * Gets code representing an enum constant.
   *
   * @return code
   */
  C getCode();
}
