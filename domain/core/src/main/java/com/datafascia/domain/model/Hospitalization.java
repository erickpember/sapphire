// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * An interaction between a patient and healthcare provider(s) for the purpose of providing
 * healthcare service(s) or assessing the health status of a patient.
 * This represents the hospitalization Element in the fhir Encounter Model.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Hospitalization")
@IdNamespace(URNFactory.NS_HOSPITALIZATION_ID)
public class Hospitalization {
  /** Pre-admission identifier. */
  @JsonProperty("@id")
  private Id<Hospitalization> id;

  /** The location from which the patient came before admission */
  @JsonProperty("originId")
  private Id<Location> originId;

  /** From where patient was admitted (physician referral, transfer). */
  @JsonProperty("admitSource")
  private CodeableConcept admitSource;

  /** Diet preferences reported by the patient. */
  @JsonProperty("dietPreference")
  private CodeableConcept dietPreference;

  /** Special courtesies (VIP, board member). */
  @JsonProperty("specialCourtesies")
  private List<CodeableConcept> specialCourtesies;

  /** Wheelchair, translator, stretcher, etc. */
  @JsonProperty("specialArrangements")
  private List<CodeableConcept> specialArrangements;

  /** Location to which the patient is discharged. */
  @JsonProperty("destinationId")
  private Id<Location> destinationId;

  /** Period during which the patient was admitted. */
  @JsonProperty("period")
  private Interval<Instant> period;

  /** Category or kind of location after discharge. */
  @JsonProperty("dischargeDisposition")
  private CodeableConcept dischargeDisposition;

  /**
   * The final diagnosis given a patient before release from the hospital after all testing,
   * surgery, and workup are complete.
   */
  @JsonProperty("dischargeDiagnosis")
  private Reference dischargeDiagnosis;

  /** Whether this hospitalization is a readmission. */
  @JsonProperty("readmission")
  private Boolean readmission;
}
