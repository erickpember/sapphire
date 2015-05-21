// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.net.URI;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.reflect.AvroSchema;

/**
 * An Element of the Hospitalization model. Represents which bed the patient will be in, and for
 * what period.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "HospitalizationAccomodation")
public class HospitalizationAccomodation {
  /** The bed that is assigned to the patient.*/
  @JsonProperty("bed")
  private URI bed;

  /** Period during which the patient was assigned the bed.*/
  @AvroSchema(Interval.INSTANT_INTERVAL_SCHEMA) @JsonProperty("period")
  private Interval<Instant> period;
}
