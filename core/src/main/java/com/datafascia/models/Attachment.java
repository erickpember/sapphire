// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.neovisionaries.i18n.LanguageCode;
import java.net.URI;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * For referring to data content defined in other formats.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName("Attachment")
public class Attachment {
  /*
   * Identifies the type of the data in the attachment and allows a method to be chosen to interpret
   * or render the data. Includes mime type parameters such as charset where appropriate.
   */
  @JsonProperty("code")
  private String contentType;

  // The human language of the content.
  @JsonProperty("language")
  private LanguageCode language;

  // The actual data of the attachment.
  @JsonProperty("data")
  private byte[] data;

  // An alternative location where the data can be accessed.
  @JsonProperty("url")
  private URI url;

  // A label or set of text to display in place of the data.
  @JsonProperty("title")
  private String title;
}
