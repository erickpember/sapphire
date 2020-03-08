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
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.Flag;
import ca.uhn.fhir.model.dstu2.valueset.FlagStatusEnum;
import java.util.List;

/**
 * Flag helper methods
 */
public class FlagUtils {

  // Private constructor disallows creating instances of this class.
  private FlagUtils() {
  }

  /**
   * Finds freshest flag.
   *
   * @param flags
   *     flags to search
   * @return
   *     freshest flag, or {@code null} if input flags is empty
   */
  public static Flag findFreshestFlag(List<Flag> flags) {
    return flags.stream()
        .max(new FlagPeriodComparator())
        .orElse(null);
  }

  /**
  * Returns true if a flag is non-null and active, otherwise false.
  *
  * @param flag
  *   flag to test
  * @return
  *   true if flag's status is active, otherwise false
  */
  public static boolean isActive(Flag flag) {
    if (flag != null &&
        flag.getStatusElement().getValueAsEnum() == FlagStatusEnum.ACTIVE) {
      return true;
    } else {
      return false;
    }
  }
}
