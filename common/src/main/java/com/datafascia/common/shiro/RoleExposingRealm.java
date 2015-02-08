// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.shiro;

import java.util.Collection;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.SecurityUtils;

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
