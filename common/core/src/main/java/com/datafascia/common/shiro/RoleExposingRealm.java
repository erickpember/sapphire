// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.shiro;

import java.util.Collection;
import java.util.Collections;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;

/**
 * Extends realm to get roles of subject.
 */
@Slf4j
public abstract class RoleExposingRealm extends AuthorizingRealm {

  // Role assigned to anonymous user
  private static final String ANONYMOUS_ROLE = "System";

  /**
   * Gets roles of the current subject.
   *
   * @return roles
   */
  public Collection<String> getRolesForSubject() {
    AuthorizationInfo authorizationInfo =
        getAuthorizationInfo(SecurityUtils.getSubject().getPrincipals());
    return (authorizationInfo != null)
        ? authorizationInfo.getRoles() : Collections.singleton(ANONYMOUS_ROLE);
  }
}
