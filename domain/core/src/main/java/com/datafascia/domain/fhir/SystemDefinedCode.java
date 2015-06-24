// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.fhir;

import com.datafascia.common.persist.Code;

/**
 * Code defined by a terminology system.
 *
 * @param <C>
 *     code representation type
 */
public interface SystemDefinedCode<C> extends Code<C> {

  /**
   * Gets identifier of the terminology system.
   *
   * @return terminology system identifier
   */
  String getSystem();
}
