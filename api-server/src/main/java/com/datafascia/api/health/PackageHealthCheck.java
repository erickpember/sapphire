// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.health;

import com.codahale.metrics.health.HealthCheck;
import lombok.extern.slf4j.Slf4j;

/**
 * Health checker for package name configuration
 */
@Slf4j
public class PackageHealthCheck extends HealthCheck {
  private final String defaultPackage;

  /**
   * Construct health checker for package name
   *
   * @param defaultPackage the package name from configuration
   */
  public PackageHealthCheck(String defaultPackage) {
    this.defaultPackage = defaultPackage;
  }

  @Override
  protected Result check() throws Exception {
    Package pkg = Package.getPackage(defaultPackage);
    if (pkg == null) {
      return Result.unhealthy("Default package name does not exist: " + defaultPackage);
    }

    return Result.healthy();
  }
}
