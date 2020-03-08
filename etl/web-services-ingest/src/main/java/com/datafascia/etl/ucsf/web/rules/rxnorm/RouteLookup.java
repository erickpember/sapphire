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
package com.datafascia.etl.ucsf.web.rules.rxnorm;

import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

/**
 * Holds a few lookups for route types.
 */
@Slf4j
public class RouteLookup {
  private static final List<String> nonRectalEnteral = Arrays.asList("15", "19", "26", "35", "68",
                                                                     "85", "86", "89", "90", "102",
                                                                     "138", "158", "159", "162",
                                                                     "187", "188");
  private static final List<String> nonJejunalEnteral = Arrays.asList("15", "19", "26", "35", "68",
                                                                      "85", "86", "90", "102",
                                                                      "138", "158", "187");
  private static final List<String> iv = Arrays.asList("11", "75", "167", "169");

  /**
   * Looks up a route returns true if that route is non-rectal.
   * @param route route to look up
   * @return true if route is non-rectal
   */
  public static boolean isNonRectalEnteral(String route) {
    return nonRectalEnteral.contains(route);
  }

  /**
   * Looks up a route and returns true if that route is non-jejunal.
   * @param route route to look up
   * @return true if route is nonjejunal
   */
  public static boolean isNonJejunalEnteral(String route) {
    return nonJejunalEnteral.contains(route);
  }

  /**
   * Looks up a route and returns true if that route is IV.
   * @param route route to look up
   * @return true if that route is IV
   */
  public static boolean isIV(String route) {
    return iv.contains(route);
  }
}
