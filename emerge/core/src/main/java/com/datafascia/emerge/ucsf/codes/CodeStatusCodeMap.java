// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact

package com.datafascia.emerge.ucsf.codes;

import java.util.HashMap;
import java.util.Map;

/**
 * Provides access to mappings of Status Codes to their respective strings.
 */
public class CodeStatusCodeMap {

  private static final Map<String, String> codeStatusCodeMap = new HashMap<String, String>() {
    {
      put("82935", "Resident Partial Code");
      put("82934", "Resident DNR/DNI");
      put("521", "Attending Partial Code");
      put("517", "Attending DNR/DNI");
      put("519", "Full Code");
    }
  };

  /**
   * Get a name for a given code.
   *
   * @param code The code to get a name for.
   * @return The name for the given code.
   */
  public static String getName(String code) {
    return codeStatusCodeMap.get(code);
  }
}
