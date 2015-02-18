// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.message;

import com.datafascia.common.persist.Id;
import com.datafascia.jackson.InstantDeserializer;
import com.datafascia.jackson.InstantSerializer;
import com.datafascia.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.hash.Hashing;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * The envelope used to transfer incoming data for start of the ETL processing. This envelope is
 * used to wrap the messages and inserted into the ingestion queue.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor @Slf4j
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "RawMessage")
public class RawMessage {
  /** Time stamp */
  @JsonProperty("timestamp")
  @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant timestamp;

   /** Institution identifier */
  @JsonProperty("institution")
  private URI institution;

  /** Facility identifier */
  @JsonProperty("facility")
  private URI facility;

  /** Department identifier */
  @JsonProperty("department")
  private URI department;

  /** Source identifier */
  @JsonProperty("source")
  private URI source;

 /** Payload type */
  @JsonProperty("payloadType")
  private URI payloadType;

  /** Payload */
  @JsonProperty("payload")
  private String payload;

  /**
   * Gets database primary key
   *
   * @return primary key
   */
  @JsonIgnore
  public Id<RawMessage> getId() {
    String id =
        getTimestamp().toString() + '|' +
        Hashing.sha1().hashString(toString(), StandardCharsets.UTF_8).toString();
    return Id.of(id);
  }
}
