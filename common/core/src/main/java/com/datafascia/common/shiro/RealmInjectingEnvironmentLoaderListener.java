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

import javax.inject.Inject;
import javax.servlet.ServletContext;
import org.apache.shiro.mgt.RealmSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.env.EnvironmentLoaderListener;
import org.apache.shiro.web.env.WebEnvironment;

/**
 * Extends {@link EnvironmentLoaderListener} to configure environment with
 * injected realm instance.
 */
public class RealmInjectingEnvironmentLoaderListener extends EnvironmentLoaderListener {

  private final Realm realm;

  /**
   * Construct with realm
   *
   * @param realm the realm
   */
  @Inject
  public RealmInjectingEnvironmentLoaderListener(Realm realm) {
    this.realm = realm;
  }

  @Override
  protected WebEnvironment createEnvironment(ServletContext servletContext) {
    WebEnvironment environment = super.createEnvironment(servletContext);
    configureRealm(environment);
    return environment;
  }

  private void configureRealm(WebEnvironment environment) {
    RealmSecurityManager realmSecurityManager =
        (RealmSecurityManager) environment.getSecurityManager();
    realmSecurityManager.setRealm(realm);
  }
}
