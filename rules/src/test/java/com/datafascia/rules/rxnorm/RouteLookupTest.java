// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.rules.rxnorm;

import java.util.Arrays;
import java.util.List;
import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Test code for RouteLookup
 */
public class RouteLookupTest {
  private static final List<String> nreRoutes = Arrays.asList("15", "19", "26", "35", "68", "85",
          "86", "89", "90", "102", "138", "158", "159", "162", "187", "188");
  private static final List<String> njeRoutes = Arrays.asList("15", "19", "26", "35", "68", "85",
          "86", "90", "102", "138", "158", "187");
  private static final List<String> ivRoutes = Arrays.asList("11", "169", "75", "167");

  /**
   * Test of isNonRectalEnteral method, of class RouteLookup.
   */
  @Test
  public void testIsNonRectalEnteral() {
    for (String route : nreRoutes) {
      assertTrue(RouteLookup.isNonRectalEnteral(route));
    }
    // Test code for rectal.
    assertFalse(RouteLookup.isNonRectalEnteral("17"));

    // Must return false on nonsense.
    assertFalse(RouteLookup.isNonRectalEnteral("nonsense"));
  }

  /**
   * Test of isNonJejunalEnteral method, of class RouteLookup.
   */
  @Test
  public void testIsNonJejunalEnteral() {
    for (String route : njeRoutes) {
      assertTrue(RouteLookup.isNonJejunalEnteral(route));
    }

    // Test code for jejunal.
    assertFalse(RouteLookup.isNonJejunalEnteral("89"));

    // Must return false on nonsense.
    assertFalse(RouteLookup.isNonJejunalEnteral("nonsense"));
  }

  /**
   * Test of isIV method, of class RouteLookup.
   */
  @Test
  public void testIsIV() {
    for (String route : ivRoutes) {
      assertTrue(RouteLookup.isIV(route));
    }
    // Test code for rectal.
    assertFalse(RouteLookup.isIV("17"));

    // Must return false on nonsense.
    assertFalse(RouteLookup.isIV("nonsense"));
  }

}
