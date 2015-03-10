// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.accumulo;

import com.datafascia.common.shiro.RoleExposingRealm;
import java.util.Collection;
import javax.inject.Inject;
import org.apache.accumulo.core.security.Authorizations;

/**
 * Supplies Accumulo authorizations based on current Shiro subject.
 */
public class SubjectAuthorizationsSupplier implements AuthorizationsSupplier {

  private final RoleExposingRealm realm;

  /**
   * Constructor
   *
   * @param realm
   *     role exposing realm
   */
  @Inject
  public SubjectAuthorizationsSupplier(RoleExposingRealm realm) {
    this.realm = realm;
  }

  @Override
  public Authorizations get() {
    Collection<String> roles = realm.getRolesForSubject();
    return new Authorizations(roles.toArray(new String[0]));
  }
}