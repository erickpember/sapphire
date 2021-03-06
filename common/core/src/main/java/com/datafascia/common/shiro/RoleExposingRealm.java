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
