// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.api.services;

import com.datafascia.models.Version;
import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Integration tests for version resources
 */
@Slf4j
public class VersionIT extends ApiIT {
  /**
   * Validate that the version is set.
   */
  @Test
  public static void testVersion() {
    Version version = api.version(MODELS_PKG);
    assertEquals(version.getId(), 1);
    assertEquals(version.getVendor(), "dataFascia Corporation");
  }
}
