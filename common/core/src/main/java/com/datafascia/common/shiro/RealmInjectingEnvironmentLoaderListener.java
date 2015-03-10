// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
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
