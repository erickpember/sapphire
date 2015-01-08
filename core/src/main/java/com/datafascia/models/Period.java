// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a span of time with a start date and end date.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
public class Period {
  @JsonProperty("start")
  private Date start;
  @JsonProperty("stop")
  private Date stop;
}
