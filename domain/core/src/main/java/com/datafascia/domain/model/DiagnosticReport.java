// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Represents a DiagnosticReport model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "DiagnosticReport")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_DIAGNOSTIC_REPORT_ID)
public class DiagnosticReport {
  /** Name/Code for this diagnostic report. */
  @JsonProperty("name")
  private CodeableConcept name;

  /** Registered | partial | final | corrected | appended | cancelled | entered-in-error. */
  @JsonProperty("status")
  private DiagnosticReportStatus status;

  /** Date this version was released. */
  @JsonProperty("issued") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant issued;

  /** The subject of this report, usualy a Patient, but could be Group, Device or Location. */
  @JsonProperty("subject")
  private DiagnosticSubject subject;

  /** Responsible DIagnostic service, either Practitioner or Organization. */
  @JsonProperty("performer")
  private DiagnosticPerformer performer;

  /** Health care event when test ordered. */
  @JsonProperty("encounterId")
  private Id<Encounter> encounterId;

  /** Id for external references to this report. */
  @JsonProperty("@id")
  private Id<DiagnosticReport> id;

  /** What was requested. */
  @JsonProperty("requestDetailIds")
  private List<Id<DiagnosticOrder>> requestDetailIds;

  /** Biochemistry, Hematology, etc. */
  @JsonProperty("serviceCategory")
  private CodeableConcept serviceCategory;

  /** Physiologically relevant time/time-period for report. */
  @JsonProperty("diagnosticDateTime") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant diagnosticDateTime;

  /** Physiologically relevant time/time-period for report. */
  @JsonProperty("diagnosticPeriod")
  private Interval<Instant> diagnosticPeriod;

  /** Specimens this report is based on. */
  @JsonProperty("specimens")
  private List<Specimen> specimens;

  /** Observations - simple, or complex nested groups. */
  @JsonProperty("resultId")
  private Id<Observation> resultId;

  /** Reference to full details of imaging associated with the diagnostic report. */
  @JsonProperty("imagingStudy")
  private List<ImagingStudy> imagingStudy;

  /** Key images associated with this report. */
  @JsonProperty("images")
  private List<DiagnosticImage> images;

  /** Clinical Interpretation of test results. */
  @JsonProperty("conclusion")
  private String conclusion;

  /** Codes for the conclusion. */
  @JsonProperty("codedDiagnoses")
  private List<CodeableConcept> codedDiagnoses;

  /** Entire Report as issued. */
  @JsonProperty("presentedForms")
  private List<Attachment> presentedForms;
}
