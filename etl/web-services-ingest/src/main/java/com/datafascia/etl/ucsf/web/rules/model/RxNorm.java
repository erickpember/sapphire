// Copyright 2020 dataFascia Corporation
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//    http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.datafascia.etl.ucsf.web.rules.model;

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
  private List<String> ahfs;

  /** Drug ID. */
  @JsonProperty("drugId")
  private String drugId;

  /** Unprocessed Pre-normalized "Drug Name". */
  @JsonProperty("drugName")
  private String drugName;

  /** RxNorm (RXCUI for TTY = IN). */
  @JsonProperty("rxcuiIn")
  private List<String> rxcuiIn = new ArrayList();
}
