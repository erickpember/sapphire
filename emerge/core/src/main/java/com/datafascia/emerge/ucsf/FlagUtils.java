// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.Flag;
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
}
