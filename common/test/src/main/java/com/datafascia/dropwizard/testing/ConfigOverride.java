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
