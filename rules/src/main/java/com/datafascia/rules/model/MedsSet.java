// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.rules.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a first pass at representing RxNorm data for matching.
 *
 * Output fields are medsSetCode and medsSetName.
 */
@Data @NoArgsConstructor
public class MedsSet {
  /** dF-internal group name. */
  @JsonProperty("code")
  private String code;

  /** Human-readable name of the group. */
  @JsonProperty("name")
  private String name;
}
