// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.etl.hl7transform;

import com.datafascia.domain.model.Race;
import com.google.common.collect.ImmutableMap;

/**
 * Provides a mapping from String to Race
 */
public class RaceMap {
  public static final ImmutableMap<String,Race> raceMap = ImmutableMap.<String, Race>builder()
      .put("b", Race.BLACK)
      .put("i", Race.AMERICAN_INDIAN)
      .put("n", Race.ASIAN)
      .put("o", Race.OTHER)
      .put("p", Race.PACIFIC_ISLANDER)
      .put("u", Race.UNKNOWN)
      .put("w", Race.WHITE)
      .build();
}
