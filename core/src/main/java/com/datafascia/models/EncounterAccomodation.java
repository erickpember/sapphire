// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents which bed the patient will be in, and for what period.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
public class EncounterAccomodation {
  // The bed that is assigned to the patient.
  @JsonProperty("bed")
  private URI bed;

  // Period during which the patient was assigned the bed.
  @JsonProperty("period")
  private Period period;
}
