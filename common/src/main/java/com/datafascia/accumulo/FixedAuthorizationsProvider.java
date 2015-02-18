// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.accumulo;

import lombok.NoArgsConstructor;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Provides the same Accumulo authorizations throughout the lifetime of the application.
 */
@NoArgsConstructor
public class FixedAuthorizationsProvider implements AuthorizationsProvider {

  private static final Authorizations AUTHORIZATIONS = new Authorizations("System");

  @Override
  public Authorizations get() {
    return AUTHORIZATIONS;
  }
}
