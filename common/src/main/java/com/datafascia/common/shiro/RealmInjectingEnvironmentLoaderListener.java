// Copyright (C) 2015 dataFascia Corporation.  All rights reserved.
// For license information, please contact http://datafascia.com/contact
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

  private Realm realm;

  @Inject
  public RealmInjectingEnvironmentLoaderListener(Realm realm) {
    this.realm = realm;
  }

  private void configureRealm(WebEnvironment environment) {
    RealmSecurityManager realmSecurityManager =
        (RealmSecurityManager) environment.getSecurityManager();
    realmSecurityManager.setRealm(realm);
  }

  @Override
  protected WebEnvironment createEnvironment(ServletContext servletContext) {
    WebEnvironment environment = super.createEnvironment(servletContext);
    configureRealm(environment);
    return environment;
  }
}
