// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.shiro;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.realm.AuthorizingRealm;

/**
 * Extends realm to get roles of subject.
 */
@Slf4j
public abstract class RoleExposingRealm extends AuthorizingRealm {

  /**
   * Gets roles of the current subject.
   *
   * @return roles
   */
  public Collection<String> getRolesForSubject() {
    return getAuthorizationInfo(SecurityUtils.getSubject().getPrincipals()).getRoles();
  }
}
