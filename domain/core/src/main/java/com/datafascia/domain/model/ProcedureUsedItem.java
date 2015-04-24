// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Part of a Procedure model, an item used during the procedure of one of these types:
 * Device, Medication, Substance
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ProcedureUsedItem")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
public class ProcedureUsedItem {
  /** Item used during the procedure. */
  @JsonProperty("deviceId")
  private Id<Device> deviceId;

  /** Item used during the procedure. */
  @JsonProperty("substanceId")
  private Id<Substance> substanceId;

  /** Item used during the procedure. */
  @JsonProperty("medicationId")
  private Id<Medication> medicationId;
}
