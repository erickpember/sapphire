// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.authenticator;

import lombok.extern.slf4j.Slf4j;
import lombok.Getter;

/**
 * API user
 */
@Slf4j @Getter
public class User {
  private final String name;
  private final String auths = "System";

  /**
   * Construct user with name
   *
   * @param name the name of the user
   */
  public User(String name) {
    this.name = name;
  }
}
