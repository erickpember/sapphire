// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.models;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import java.net.MalformedURLException;
import java.net.URL;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

/**
 * Defines a link to an external resource
 *
 * @param <T> the type the link points to
 */
@Slf4j @EqualsAndHashCode @NoArgsConstructor @AllArgsConstructor @Getter @Setter @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Link")
public class Link<T> {
  /** Externally retrievable URL of the resource */
  @JsonProperty("href")
  private URL href;

  /** Relation with external resource from this link */
  @JsonProperty("relation")
  private LinkRelation relation;

  /** Mime type of external resource */
  @JsonProperty("type")
  private String resourceType;

  /** Title of the external resource */
  @JsonProperty("title") @JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
  private String title;

  /**
   * Convenience method constructs link from string representation.
   *
   * @param <T> type of link identified by this URL
   * @param url the URL of the external resource is required
   * @param relation optionally the relation to the external resource
   * @param type optionally the type of the external resource
   * @param title optionally the title of the external resource
   *
   * @return the link
   *
   */
  public static <T> Link<T> of(URL url, LinkRelation relation, String type, String title) {
    return new Link<>(url, relation, type, title);
  }

  /**
   * Convenience method constructs link from string representation.
   *
   * @param <T> type of link identified by this URL
   * @param url the URL string spec of the external resource is required
   * @param relation optionally the relation to the external resource
   * @param type optionally the type of the external resource
   * @param title optionally the title of the external resource
   *
   * @return the link
   *
   * @throws MalformedURLException in case of invalid URL string representation
   */
  public static <T> Link<T> of(String url, LinkRelation relation, String type, String title)
      throws MalformedURLException {
    return new Link<>(new URL(url), relation, type, title);
  }
}
