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
 * Represents a Specimen model.
 */
@Data @NoArgsConstructor @JsonTypeName(URNFactory.MODEL_PREFIX + "Specimen")
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@IdNamespace(URNFactory.NS_SPECIMEN_ID)
public class Specimen {
  /** External identifier. */
  @JsonProperty("@id")
  private Id<Specimen> id;

  /** Kind of material that forms the specimen. */
  @JsonProperty("type")
  private CodeableConcept type;

  /** Specimen(s) from which this specimen originated. */
  @JsonProperty("parentIds")
  private List<Id<Specimen>> parentIds;

  /** Where the specimen came from. Can be of type Patient | Group | Device | Substance. */
  @JsonProperty("subject")
  private SpecimenSubject subject;

  /** Identifier assigned by the lab. */
  @JsonProperty("accessionIdentifier")
  private String accessionIdentifier;

  /** The time the specimen was received for processing. */
  @JsonProperty("receivedTime") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant receivedTime;

  /** Who collected the specimen. */
  @JsonProperty("collectorId")
  private Id<Practitioner> collectorId;

  /** Collector comments. */
  @JsonProperty("collectionComments")
  private List<String> collectionComments;

  /** Collection time, as date/time. */
  @JsonProperty("collectedDateTime") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant collectedDateTime;

  /** Collection time, as interval. */
  @JsonProperty("collectedPeriod")
  private Interval<Instant> collectedPeriod;

  /** The quantity of specimen collected. */
  @JsonProperty("collectedQuantity")
  private NumericQuantity collectedQuantity;

  /** Technique used to perform collection. */
  @JsonProperty("collectionMethod")
  private CodeableConcept collectionMethod;

  /** Anatomical collection site, as codeable concept. */
  @JsonProperty("collectionBodySiteCodableConcept")
  private CodeableConcept collectionBodySiteCodableConcept;

  /** Anatomical collection site, as BodySite reference. */
  @JsonProperty("collectionBodySiteReferenceId")
  private Id<BodySite> collectionBodySiteReferenceId;

  /** Treatment and processing step details. */
  @JsonProperty("treatments")
  private SpecimenTreatment treatments;

  /** Direct container of specimen, tube/slide etc. */
  @JsonProperty("containers")
  private List<SpecimenContainer> containers;
}
