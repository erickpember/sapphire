// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.DurationDeserializer;
import com.datafascia.common.jackson.DurationSerializer;
import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.time.Duration;
import java.time.Instant;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.apache.avro.reflect.AvroSchema;

/**
 * Holds a given value for an observation. This one value could be of any of the given
 * types.
 */
@Data @NoArgsConstructor @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "ObservationValue")
public class ObservationValue {
  /** Specific UCUM measurement.*/
  @JsonProperty("quantity")
  private NumericQuantity quantity;

  /** Code identifying what is being observed. */
  @JsonProperty("codeableConcept")
  private CodeableConcept codeableConcept;

  /** A raw string value. */
  @JsonProperty("string")
  private String string;

  /** The observation value in Range form. */
  @JsonProperty("range")
  private Range range;

  /** Ratio of values associated with the observation.*/
  @JsonProperty("ratio")
  private Ratio ratio;

  /** Data sampled from a device.*/
  @JsonProperty("sampleData")
  private SampledData sampledData;

  /** Data associated with the observation. */
  @JsonProperty("attachment")
  private Attachment attachment;

  /** Observation value as time of day. Stored as duration since midnight. */
  @JsonProperty("time") @JsonSerialize(using = DurationSerializer.class)
  @JsonDeserialize(using = DurationDeserializer.class)
  private Duration time;

  /** Observation value as Date+Time. */
  @JsonProperty("dateTime") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant dateTime;

  /** Range of time associated with the observation. */
  @AvroSchema(Interval.INSTANT_INTERVAL_SCHEMA) @JsonProperty("period")
  private Interval<Instant> period;
}
