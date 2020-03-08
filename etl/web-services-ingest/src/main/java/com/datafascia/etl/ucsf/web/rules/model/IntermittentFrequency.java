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
package com.datafascia.etl.ucsf.web.rules.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a first pass at representing RxNorm data for matching.
 */
@Data @NoArgsConstructor @AllArgsConstructor
public class IntermittentFrequency {
  /** Frequency ID. */
  @JsonProperty(value = "id", index = 0)
  private String id;

  /** Frequency Name. */
  @JsonProperty(value = "name", index = 1)
  private String name;

  /** Frequency Display Name. */
  @JsonProperty(value = "displayName", index = 2)
  private String displayName;

  /** Is this "Any Intermittent Frequency"?. */
  @JsonProperty(value = "intermittent", index = 3)
  private boolean intermittent;

  /** Is this a Daily frequency?. */
  @JsonProperty(value = "daily", index = 4)
  private boolean daily;

  /** Is this a twice daily frequency?. */
  @JsonProperty(value = "twiceDaily", index = 5)
  private boolean twiceDaily;

  /** Is this a "Once Frequency"?. */
  @JsonProperty(value = "once", index = 6)
  private boolean once;
}
