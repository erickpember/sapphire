// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.persist.Id;
import com.datafascia.common.urn.URNFactory;
import com.datafascia.common.urn.annotations.IdNamespace;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Representation of the content produced in a DICOM imaging study.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "ImagingStudy")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_IMAGING_STUDY_ID)
public class ImagingStudy {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<ImagingStudy> id;

  /** When the study was started. */
  @JsonProperty("started") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant started;

  /** Who the images are of. */
  @JsonProperty("patientId")
  private Id<Patient> patientId;

  /** Formal identifier for the study. */
  @JsonProperty("oid")
  private Id<Oid> oid;

  /** Accession number. */
  @JsonProperty("accession")
  private URI accession;

  /** Order(s) that caused this study to be performed. */
  @JsonProperty("orderIds")
  private List<Id<DiagnosticOrder>> orderIds;

  /** All series.modality if actual acquisition modalities. */
  @JsonProperty("modalityList")
  private List<CodeableConcept> modalityList;

  /** Referring physician. */
  @JsonProperty("referrerId")
  private Id<Practitioner> referrerId;

  /** ONLINE | OFFLINE | NEARLINE | UNAVAILABLE. */
  @JsonProperty("availability")
  private ImagingStudyAvailability availability;

  /** Retrieve URI. */
  @JsonProperty("url")
  private URI url;

  /** Number of study related series. */
  @JsonProperty("numberOfSeries")
  private BigDecimal numberOfSeries;

  /** Number of study related instances. */
  @JsonProperty("numberOfInstances")
  private BigDecimal numberOfInstances;

  /** Diagnoses etc with request. */
  @JsonProperty("clinicalInformation")
  private String clinicalInformation;

  /** Type of procedure performed. */
  @JsonProperty("procedures")
  private List<Coding> procedures;

  /** Interpreter of imaging study. */
  @JsonProperty("interpreterId")
  private Id<Practitioner> interpreterId;

  /** Institution-generated description. */
  @JsonProperty("description")
  private String description;

  /** Each study has one or more series of instances. */
  @JsonProperty("series")
  private ImagingStudySeries series;
}
