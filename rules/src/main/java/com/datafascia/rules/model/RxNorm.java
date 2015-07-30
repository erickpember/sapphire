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
public class RxNorm {
  /** dF-internal group name. */
  @JsonProperty("medsSetCode")
  private String medsSetCode;

  /** Human-readable name of the group. */
  @JsonProperty("medsSetName")
  private String medsSetName;

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
  @JsonProperty("brand")
  private String brand;

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
