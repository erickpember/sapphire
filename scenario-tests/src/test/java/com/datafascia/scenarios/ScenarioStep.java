// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.scenarios;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * A single step to be executed for a scenario
 */
@Slf4j @NoArgsConstructor @Getter @Setter @ToString @EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("ScenarioStep")
public class ScenarioStep {
  /** Description for the scenario step */
  @JsonProperty("description")
  String description;

  /** File containing the message to be processed */
  @JsonProperty("messageFile")
  String messageFile;

  /** Wait interval for message to be processed */
  @JsonProperty("waitInterval")
  Integer waitInterval;

  /** Patient status */
  @JsonProperty("patientStatus")
  Boolean patientStatus;
}
