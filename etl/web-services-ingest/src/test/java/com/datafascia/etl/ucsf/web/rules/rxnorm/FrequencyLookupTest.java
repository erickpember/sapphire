// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.ucsf.web.rules.rxnorm;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/**
 * Tests FrequencyLookup
 */
public class FrequencyLookupTest {

  public FrequencyLookupTest() {
  }

  /**
   * Test of isIntermittent method, of class FrequencyLookup.
   */
  @Test
  public void testIsIntermittent() {
    assertTrue(FrequencyLookup.isIntermittent("200553"));
    assertFalse(FrequencyLookup.isIntermittent("200905"));
    assertFalse(FrequencyLookup.isIntermittent("false"));
  }

  /**
   * Test of isDaily method, of class FrequencyLookup.
   */
  @Test
  public void testIsDaily() {
    assertTrue(FrequencyLookup.isDaily("200507"));
    assertFalse(FrequencyLookup.isDaily("200502"));
    assertFalse(FrequencyLookup.isDaily("false"));
  }

  /**
   * Test of isTwiceDaily method, of class FrequencyLookup.
   */
  @Test
  public void testIsTwiceDaily() {
    assertTrue(FrequencyLookup.isTwiceDaily("200800"));
    assertFalse(FrequencyLookup.isTwiceDaily("200812"));
    assertFalse(FrequencyLookup.isTwiceDaily("false"));
  }

  /**
   * Test of isOnce method, of class FrequencyLookup.
   */
  @Test
  public void testIsOnce() {
    assertTrue(FrequencyLookup.isOnce("200812"));
    assertFalse(FrequencyLookup.isOnce("200001"));
    assertFalse(FrequencyLookup.isOnce("false"));
  }

  /**
   * Test of isContinuous method, of class FrequencyLookup.
   */
  @Test
  public void testIsContinuous() {
    assertTrue(FrequencyLookup.isContinuous("200905"));
    assertFalse(FrequencyLookup.isContinuous("200001"));
    assertFalse(FrequencyLookup.isContinuous("false"));
  }
}
