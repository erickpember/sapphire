// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
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
