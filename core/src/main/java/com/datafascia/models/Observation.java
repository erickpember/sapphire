// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.net.URI;
import java.util.Date;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

/**
 * Represents a patient observation.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
public class Observation {
  /** Unique identifier for observation */
  @JsonProperty("id")
  private URI id;

  /** Describes what was observed. Sometimes this is called the observation "code". */
  @JsonProperty("name")
  private CodeableConcept name;

  /**
   * The information determined as a result of making the observation, if the information has a
   * simple value.
   */
  @JsonProperty("values")
  private ObservationValue values;

  /** The assessment made based on the result of the observation. */
  @JsonProperty("interpretation")
  private ObservationInterpretation interpretation;

  /**
   * May include statements about significant, unexpected or unreliable values, or information about
   * the source of the value where this may be relevant to the interpretation of the result.
   */
  @JsonProperty("comments")
  private String comments;

  /**
   * The time or time-period the observed value is asserted as being true. For biological subjects -
   * e.g. human patients - this is usually called the &quot;physiologically relevant time&quot;.
   * This is usually either the time of the procedure or of specimen collection, but very often the
   * source of the date/time is not known, only the date/time itself.
   */
  @JsonProperty("applies")
  private Period  applies;

  /** Date/Time this was made available. */
  @JsonProperty("issued")
  private Date issued;

  /** The status of the result value. */
  @JsonProperty("status")
  private ObservationStatus status;

  /** An estimate of the degree to which quality issues have impacted on the value reported. */
  @JsonProperty("reliability")
  private ObservationReliability reliability;

  /** Indicates where on the subject's body the observation was made. */
  @JsonProperty("site")
  private CodeableConcept site;

  /** Indicates the mechanism used to perform the observation. */
  @JsonProperty("method")
  private CodeableConcept method;

  /** A unique identifier for the simple observation. */
  @JsonProperty("identifier")
  private CodeableConcept identifier;

  /** The thing the observation is being made about. */
  @JsonProperty("subject")
  private URI subject;

  /** The specimen that was used when this observation was made. */
  @JsonProperty("specimen")
  private URI specimen;

  /** Who was responsible for asserting the observed value as "true". */
  @JsonProperty("performer")
  private URI performer;

  /** Guidance on how to interpret the value by comparison to a normal or recommended range. */
  @JsonProperty("range")
  private List<ReferenceRange> range;

  /**
   * Related observations - either components, or previous observations, or statements of
   * derivation.
   */
  @JsonProperty("relatedTo")
  private List<ObservationRelated> relatedTo;
}
