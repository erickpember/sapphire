// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.string;

import java.io.UnsupportedEncodingException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;

/**
 * Utility string functions
 */
@Slf4j
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

  /**
   * Convert string to base64 form
   *
   * @param string the string
   */
  public static String base64Encode(String string) {
    try {
      byte[] creds = Base64.encodeBase64(string.getBytes("UTF-8"));

      return new String(creds, "UTF-8");
    } catch (UnsupportedEncodingException exp) {
      log.error("UTF-8 needs to be supported", exp);
      throw new RuntimeException(exp);
    }
  }
}
