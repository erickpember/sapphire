// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.string;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility string functions
 */
public class StringUtils {
  /**
   * Remove whitespace and quotes from start and end of string
   *
   * @param string the string to clean
   *
   * @return the cleaned string
   */
  public static String trimQuote(String string) {
    String str = string.trim();
    if (str.startsWith("\"")) {
      str = str.substring(1, str.length());
    }
    if (str.endsWith("\"")) {
      str = str.substring(0, str.length() - 1);
    }

    return str;
  }
}
