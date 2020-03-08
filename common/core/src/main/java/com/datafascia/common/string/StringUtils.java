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
package com.datafascia.common.string;

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
