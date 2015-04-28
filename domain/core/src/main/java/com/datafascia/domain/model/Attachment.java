// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.jackson.InstantDeserializer;
import com.datafascia.common.jackson.InstantSerializer;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.neovisionaries.i18n.LanguageCode;
import java.math.BigDecimal;
import java.net.URI;
import java.time.Instant;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * For referring to data content defined in other formats.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Attachment")
public class Attachment {
  /**
   * Identifies the type of the data in the attachment and allows a method to be chosen to interpret
   * or render the data. Includes mime type parameters such as charset where appropriate.
   */
  @JsonProperty("contentType")
  private String contentType;

  /** The human language of the content.*/
  @JsonProperty("language")
  private LanguageCode language;

  /** The actual data of the attachment.*/
  @JsonProperty("data")
  private byte[] data;

  /** An alternative location where the data can be accessed.*/
  @JsonProperty("url")
  private URI url;

  /** Number of bytes of content (if url is provided). */
  @JsonProperty("size")
  private BigDecimal size;

  /** Hash of the data (sha-1, base64ed). */
  @JsonProperty("hash")
  private byte[] hash;

  /** Date attachment was first created. */
  @JsonProperty("creation") @JsonSerialize(using = InstantSerializer.class)
  @JsonDeserialize(using = InstantDeserializer.class)
  private Instant creation;

  /** A label or set of text to display in place of the data.*/
  @JsonProperty("title")
  private String title;
}
