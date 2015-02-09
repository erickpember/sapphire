// Copyright (C) 2014-2015 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.dropwizard.testing;

import java.util.Enumeration;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to override properties
 */
@Slf4j @RequiredArgsConstructor(staticName = "config")
public class ConfigOverride {
  /**
   * Prefix for system property key
   */
  public static final String PROP_PREFIX = "dw.";

  private final String key;
  private final String value;

  /**
   * Override the specific property
   */
  public void override() {
    log.info("Overriding property: " + key);
    System.setProperty(PROP_PREFIX + key, value);
  }

  /**
   * Reset the properties back
   */
  public static void reset() {
    for (Enumeration<?> props = System.getProperties().propertyNames(); props.hasMoreElements(); ) {
      String keyString = (String) props.nextElement();
      if (keyString.startsWith(PROP_PREFIX)) {
        log.info("Resetting property: " + keyString.substring(PROP_PREFIX.length()));
        System.clearProperty(keyString);
      }
    }
  }
}
