// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
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

  /**
   * Constructor
   */
  public FakeRealm() {
    setCachingEnabled(false);
  }

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
