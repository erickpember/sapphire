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
