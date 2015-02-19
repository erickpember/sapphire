// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.string;

import java.io.UnsupportedEncodingException;
import java.util.Base64;
import lombok.extern.slf4j.Slf4j;

/**
 * Utility string functions
 */
@Slf4j
public class StringUtils {

  /**
   * @param string the string
   *
   * @return the string in Base64 form
   */
  public static String base64Encode(String string) {
    try {
      return Base64.getEncoder().encodeToString(string.getBytes("utf-8"));
    } catch (UnsupportedEncodingException ex) {
      log.error("UTF-8 needs to be supported", ex);
      throw new RuntimeException(ex);
    }
  }

  private StringUtils() {
  }
}
