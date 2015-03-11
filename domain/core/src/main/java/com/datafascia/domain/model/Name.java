// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.stringtemplate.v4.ST;

/**
 * Represents a person's name, with a first, middle, and last name.
 */
@Slf4j @NoArgsConstructor @Getter @Setter @EqualsAndHashCode @ToString
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "Name")
public class Name {
  /** First name format identifier */
  public static final String FIRST_FM = "<first>";
  /** Middle name format identifier */
  public static final String MIDDLE_FM = "<middle>";
  /** Last name format identifier */
  public static final String LAST_FM = "<last>";

  /** First name property */
  public static final String FIRST = "first";
  /** Middle name property */
  public static final String MIDDLE = "middle";
  /** Last name property */
  public static final String LAST = "last";

  @JsonProperty(FIRST)
  private String first;
  @JsonProperty(MIDDLE)
  private String middle;
  @JsonProperty(LAST)
  private String last;

  /**
   * Return name in order requested in passed format. So for example, the calls can be made:
   *
   * name = new Name();
   * // set parameters
   * name.format("Hello <first> <last>!");
   * name.format("Hello <first>");
   * name.format("<last>, <first>");
   * name.format("<last>, <middle> <first>");
   *
   * @param format the order in which to return the name
   *
   * @return the name in the expected format filled in
   */
  public String format(String format) {
    ST name = new ST(format);
    name.add(FIRST, first);
    name.add(MIDDLE, middle);
    name.add(LAST, last);

    return name.render();
  }
}
