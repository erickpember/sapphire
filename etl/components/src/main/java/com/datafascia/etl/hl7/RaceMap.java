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
