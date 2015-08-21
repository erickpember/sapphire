// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

import lombok.NoArgsConstructor;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Supplies empty Accumulo authorizations always.
 */
@NoArgsConstructor
public class EmptyAuthorizationsSupplier implements AuthorizationsSupplier {

  private static final Authorizations AUTHORIZATIONS = new Authorizations();

  @Override
  public Authorizations get() {
    return AUTHORIZATIONS;
  }
}
