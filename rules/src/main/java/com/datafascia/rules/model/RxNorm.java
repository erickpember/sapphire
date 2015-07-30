// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.rules.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents both input and output for mapping RxNorm codes to logical groups needed by the
 * Client.
 *
 * Output type is medsSet. The rest are input.
 */
@Data @NoArgsConstructor
public class RxNorm {
  /** dF-internal group name and code. */
  @JsonProperty("medsSets")
  private List<MedsSet> medsSets = new ArrayList();

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
