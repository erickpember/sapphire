// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.string;

import lombok.extern.slf4j.Slf4j;
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

/**
 * Unit tests for string utilities
 */
public class StringUtilsTest {
  @Test
  public void withQuotes() {
    assertEquals(StringUtils.trimQuote("\"test\""), "test");
  }

  @Test
  public void withoutQuote() {
    assertEquals(StringUtils.trimQuote("test"), "test");
  }
}
