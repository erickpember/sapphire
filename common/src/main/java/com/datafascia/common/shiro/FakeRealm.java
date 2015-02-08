// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.common.shiro;

import java.util.Collections;
import javax.inject.Singleton;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.subject.PrincipalCollection;

/**
 * Fake realm not intended for production use.
 */
@Singleton
public class FakeRealm extends RoleExposingRealm {

  private static final String REALM_NAME = FakeRealm.class.getSimpleName();
  private static final String DEFAULT_ROLE = "System";

  @Override
  protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token)
      throws AuthenticationException {

    UsernamePasswordToken usernamePasswordToken = (UsernamePasswordToken) token;
    String password = String.valueOf(usernamePasswordToken.getPassword());
    if (!"supersecret".equals(password)) {
      throw new IncorrectCredentialsException();
    }

    return new SimpleAuthenticationInfo(
        usernamePasswordToken.getUsername(), usernamePasswordToken.getCredentials(), REALM_NAME);
  }

  @Override
  protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
    return new SimpleAuthorizationInfo(Collections.singleton(DEFAULT_ROLE));
  }
}
