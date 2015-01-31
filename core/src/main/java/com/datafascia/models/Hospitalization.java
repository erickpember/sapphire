// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.urn.annotations.IDNamespace;
import com.datafascia.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * An interaction between a patient and healthcare provider(s) for the purpose of providing
 * healthcare service(s) or assessing the health status of a patient.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("Hospitalization") @IDNamespace(URNFactory.NS_HOSPITALIZATION_ID)
public class Hospitalization {
  /** Pre-admission identifier. */
  @JsonProperty("@id")
  private URI id;

  /** The location from which the patient came befor admission */
  @JsonProperty("origin")
  private URI origin;

  /** From where patient was admitted (physician referral, transfer). */
  @JsonProperty("admitSource")
  private CodeableConcept admitSource;

  /** Period during which the patient was admitted. */
  @JsonProperty("period")
  private Period period;

  /** Where the patient stays during this encounter. */
  @JsonProperty("accomodation")
  private EncounterAccomodation accomodation;

  /** Dietary restrictions for the patient. */
  @JsonProperty("diet")
  private CodeableConcept diet;

  /** Special courtesies (VIP, board member). */
  @JsonProperty("specialCourtesy")
  private CodeableConcept specialCourtesy;

  /** Wheelchair, translator, stretcher, etc. */
  @JsonProperty("specialArrangement")
  private CodeableConcept specialArrangement;

  /** Location to which the patient is discharged. */
  @JsonProperty("destination")
  private URI destination;

  /** Category or kind of location after discharge. */
  @JsonProperty("dischargeDisposition")
  private CodeableConcept dischargeDisposition;

  /**
   * The final diagnosis given a patient before release from the hospital after all testing,
   * surgery, and workup are complete.
   */
  @JsonProperty("dischargeDiagnosis")
  private URI dischargeDiagnosis;

  /** Whether this hospitalization is a readmission. */
  @JsonProperty("readmission")
  private boolean readmission;
}
