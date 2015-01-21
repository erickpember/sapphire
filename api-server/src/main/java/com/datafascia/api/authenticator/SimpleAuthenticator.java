// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.authenticator;

import com.google.common.base.Optional;
import io.dropwizard.auth.AuthenticationException;
import io.dropwizard.auth.Authenticator;
import io.dropwizard.auth.basic.BasicCredentials;

/**
 * Simple authenticator for API access
 */
public class SimpleAuthenticator implements Authenticator<BasicCredentials, User> {
  @Override
  public Optional<User> authenticate(BasicCredentials credentials) throws AuthenticationException {
    if ("supersecret".equals(credentials.getPassword())) {
      return Optional.of(new User(credentials.getUsername()));
    }

    return Optional.absent();
  }
}

