// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.rules.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a first pass at representing RxNorm data for matching.
 */
@Data @NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "RxNorm")
public class RxNorm {
  /** dF-internal group name. */
  @JsonProperty("medsGroup")
  private String medsGroup;

  /** Human-readable name of the group. */
  @JsonProperty("groupName")
  private String groupName;

  /** Maps to MedicationPrescription.route. */
  @JsonProperty("route")
  private String route;

  /** Maps to MedicationPrescription.dosageInstruction.scheduled.scheduledTiming.. */
  @JsonProperty("frequency")
  private String frequency;

  /** Yes|No|N/A. */
  @JsonProperty("pca")
  private String pca;

  /** Drug brand name. */
  @JsonProperty("brandName")
  private String brandName;

  /** Name of drug. */
  @JsonProperty("rxcuiSCD")
  private String rxcuiSCD;

  /** AHFS class number. */
  @JsonProperty("ahfs")
  private String ahfs;

  /** Drug ID. */
  @JsonProperty("drugId")
  private String drugId;

  /** RxNorm (RXCUI for TTY = IN). */
  @JsonProperty("rxcuiIn")
  private String rxcuiIn;
}
