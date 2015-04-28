// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.scenarios;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Scenario description
 */
@Slf4j @NoArgsConstructor @Getter @Setter @ToString @EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("Scenario")
public class Scenario {
  /** Description for the scenario */
  @JsonProperty("description")
  String description;

  /** Identifier for the patient */
  @JsonProperty("patientId")
  String patientId;

  /** Institution identifier for the patient */
  @JsonProperty("institutionId")
  String institutionId;

  /** The steps to execute for the scenario */
  @JsonProperty("steps")
  List<ScenarioStep> steps;
}
