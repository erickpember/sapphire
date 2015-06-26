// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7;

import com.datafascia.domain.fhir.RaceEnum;
import com.google.common.collect.ImmutableMap;

/**
 * Provides a mapping from String to Race
 */
public class RaceMap {
  public static final ImmutableMap<String, RaceEnum> raceMap =
      ImmutableMap.<String, RaceEnum>builder()
          .put("b", RaceEnum.BLACK)
          .put("i", RaceEnum.AMERICAN_INDIAN)
          .put("n", RaceEnum.ASIAN)
          .put("o", RaceEnum.OTHER)
          .put("p", RaceEnum.PACIFIC_ISLANDER)
          .put("u", RaceEnum.UNKNOWN)
          .put("w", RaceEnum.WHITE)
          .build();
}
