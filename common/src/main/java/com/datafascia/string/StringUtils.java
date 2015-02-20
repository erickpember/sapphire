// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.string;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Utility string functions
 */
public class StringUtils {

  /**
   * Base 64 encodes UTF-8 formatted string
   *
   * @param string the string
   * @return the string in base 64 form
   */
  public static String base64Encode(String string) {
    return Base64.getEncoder().encodeToString(string.getBytes(StandardCharsets.UTF_8));
  }

  // Private constructor disallows creating instances of this class
  private StringUtils() {
  }
}
