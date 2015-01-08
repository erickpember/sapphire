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
 * Identifies a participant in an encounter.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
public class Participant {
  // Role of participant in encounter.
  @JsonProperty("role")
  private CodeableConcept role;

  // Persons involved in the encounter other than the patient.
  @JsonProperty("individual")
  private URI individual;
}
