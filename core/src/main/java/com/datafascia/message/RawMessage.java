// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.message;

import com.datafascia.jackson.InstantDeserializer;
import com.datafascia.jackson.InstantSerializer;
import com.datafascia.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.URI;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * The envelope used to transfer incoming data for start of the ETL processing. This envelope is
 * used to wrap the messages and inserted into the ingestion queue.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "RawMessage")
public class RawMessage {
  /** Time stamp */
  @JsonProperty("timestamp")
  @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  Instant timestamp;

   /** Institution identifier */
  @JsonProperty("institution")
  URI institution;

  /** Facility identifier */
  @JsonProperty("facility")
  URI facility;

  /** Department identifier */
  @JsonProperty("department")
  URI department;

  /** Source identifier */
  @JsonProperty("source")
  URI source;

 /** Payload type */
  @JsonProperty("payloadType")
  URI payloadType;

  /** Payload */
  @JsonProperty("payload")
  String payload;
}
