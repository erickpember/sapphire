// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.emerge.ucsf;

import ca.uhn.fhir.model.dstu2.resource.Observation;
import java.util.List;

/**
 * Pain helper methods
 */
public class PainUtils {

  // Private constructor disallows creating instances of this class.
  private PainUtils() {
  }

  /**
   * Finds freshest pain type.
   *
   * @param observations
   *     observations to search
   * @return freshest pain type, or {@code null} if no pain observations found
   */
  public static String findFreshestPainType(List<Observation> observations) {
    observations.sort(new ObservationEffectiveComparator().reversed());

    for (Observation observation : observations) {
      String code = observation.getCode().getCodingFirstRep().getCode();
      switch (code) {
        case "304890008":
        case "304890009":
        case "304890010":
        case "304890011":
          return "Numerical Level of Pain Assessments";

        case "304894105":
        case "304890004":
        case "304890005":
        case "304890006":
          return "Acceptable Level of Pain Assessments";

        case "304890012":
        case "304890013":
        case "304890014":
        case "304890015":
          return "Verbal Descriptor Level of Pain Assessments";

        case "304890016":
          return "Critical-Care Pain Observation Tool (CPOT) Total";
      }
    }

    return null;
  }
}
