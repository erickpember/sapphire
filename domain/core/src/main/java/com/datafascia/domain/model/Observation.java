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
import lombok.ToString;
import org.apache.avro.reflect.AvroSchema;

/**
 * Represents a patient observation.
 */
@Data @NoArgsConstructor @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Observation") @IdNamespace(URNFactory.NS_OBSERVATION_ID)
public class Observation {
  /** Type of observation (code / type). */
  @JsonProperty("code")
  private CodeableConcept code;

  /**
   * The information determined as a result of making the observation, if the information has a
   * simple value.
   */
  @JsonProperty("value")
  private ObservationValue value;

  /** Why the result is missing. */
  @JsonProperty("dataAbsentReason")
  private CodeableConcept dataAbsentReason;

  /** The assessment made based on the result of the observation. */
  @JsonProperty("interpretation")
  private CodeableConcept interpretation;

  /**
   * May include statements about significant, unexpected or unreliable values, or information about
   * the source of the value where this may be relevant to the interpretation of the result.
   */
  @JsonProperty("comments")
  private String comments;

  /**
   * The time or time-period the observed value is asserted as being true. For biological subjects -
   * e.g. human patients - this is usually called the "physiologically relevant time"
   * This is usually either the time of the procedure or of specimen collection, but very often the
   * source of the date/time is not known, only the date/time itself.
   */
  @AvroSchema(Interval.INSTANT_INTERVAL_SCHEMA) @JsonProperty("appliesPeriod")
  private Interval<Instant> appliesPeriod;

  /**
   * The time or time-period the observed value is asserted as being true. Stored as
   * Date+Time or Period.
   */
  @JsonProperty("appliesDateTime") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant appliesDateTime;

  /** Date/Time this was made available. */
  @JsonProperty("issued") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant issued;

  /** The status of the result value. */
  @JsonProperty("status")
  private ObservationStatus status;

  /** An estimate of the degree to which quality issues have impacted on the value reported. */
  @JsonProperty("reliability")
  private ObservationReliability reliability;

  /** Observed body part, as code. */
  @JsonProperty("bodySiteCodeableConcept")
  private CodeableConcept bodySiteCodeableConcept;

  /** Observed body part, as reference to BodySite model. */
  @JsonProperty("bodySiteReferenceId")
  private Id<BodySite> bodySiteReferenceId;

  /** Indicates the mechanism used to perform the observation. */
  @JsonProperty("method")
  private CodeableConcept method;

  /** Unique identifier for observation */
  @JsonProperty("@id")
  private Id<Observation> id;

  /** A unique identifier for the simple observation. */
  @JsonProperty("identifier")
  private CodeableConcept identifier;

  /** The thing the observation is being made about. */
  @JsonProperty("subject")
  private ObservationSubject subject;

  /** The specimen that was used when this observation was made. */
  @JsonProperty("specimenId")
  private Id<Specimen> specimenId;

  /** Who was responsible for asserting the observed value as "true". */
  @JsonProperty("performers")
  private List<ObservationPerformer> performers;

  /** (Measurement) Device. */
  @JsonProperty("device")
  private ObservationDevice device;

  /** Healthcare event during which this observation is made. */
  @JsonProperty("encounterId")
  private Id<Encounter> encounterId;

  /** Guidance on how to interpret the value by comparison to a normal or recommended range. */
  @JsonProperty("referenceRanges")
  private List<ObservationReferenceRange> referenceRanges;

  /** Describes what was observed. Sometimes this is called the observation "code". */
  @JsonProperty("name")
  private CodeableConcept name;

  /**
   * Related observations - either components, or previous observations, or statements of
   * derivation.
   */
  @JsonProperty("related")
  private List<ObservationRelated> related;
}
