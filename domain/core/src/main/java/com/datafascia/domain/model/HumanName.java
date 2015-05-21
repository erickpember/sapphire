// Copyright (C) 2015-2016 dataFascia Corporation - All Rights Reserved
// For license information, please contact http://datafascia.com/contact
package com.datafascia.domain.model;

import com.datafascia.common.time.Interval;
import com.datafascia.common.urn.URNFactory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.avro.reflect.AvroSchema;
import org.stringtemplate.v4.ST;

/**
 * A name of a human with text, parts and usage information.
 */
@AllArgsConstructor @Builder @Data @NoArgsConstructor
@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "@type")
@JsonTypeName(URNFactory.MODEL_PREFIX + "HumanName")
public class HumanName {
  /** Given name format identifier */
  public static final String GIVEN_FM = "<given>";
  /** Prefix name format identifier */
  public static final String PREFIX_FM = "<prefix>";
  /** Suffix name format identifier */
  public static final String SUFFIX_FM = "<suffix>";
  /** Family name format identifier */
  public static final String FAMILY_FM = "<family>";

  /** Given name property, not always 'first', includes middle names. */
  public static final String GIVEN = "given";
  /** Prefix name property, parts that come before the name. */
  public static final String PREFIX = "prefix";
  /** Suffix name property, parts that come after the name. */
  public static final String SUFFIX = "suffix";
  /** Family name property, often called 'Surname'. */
  public static final String FAMILY = "family";

  @JsonProperty(GIVEN)
  private List<String> given;
  @JsonProperty(PREFIX)
  private List<String> prefix;
  @JsonProperty(SUFFIX)
  private List<String> suffix;
  @JsonProperty(FAMILY)
  private List<String> family;

  /** Purpose of the name: usual | official | temp | nickname | anonymous | old | maiden. */
  @JsonProperty("use")
  private HumanNameUse use;

  /** Text representation of the full name. */
  @JsonProperty("text")
  private String text;

  /** Time period when name was/is in use. */
  @AvroSchema(Interval.INSTANT_INTERVAL_SCHEMA) @JsonProperty("period")
  private Interval<Instant> period;

  /**
   * Return name in order requested in passed format. So for example, the calls can be made:
   *
   * name = new HumanName();
   * // set parameters
   *
   * name.format("Hello <given> <family>!");
   * name.format("Hello <given>");
   * name.format("<family>, <given>");
   * name.format("<family>, <prefix> <given>");
   *
   * @param format the order in which to return the name
   *
   * @return the name in the expected format filled in
   */
  public String format(String format) {
    ST name = new ST(format);
    name.add(PREFIX, prefix);
    name.add(GIVEN, given);
    name.add(FAMILY, family);

    return name.render();
  }

  /**
   * FHIR stores middle names as 2nd (and 3rd, 4th, .. nth) given names.
   * This takes the thinking out of retrieval.
   *
   * @return The middle name, stored in second subscript of the given name.
   */
  @JsonIgnore
  public String getMiddleName() {
    if (given != null && given.size() > 1) {
      return given.get(1);
    } else {
      return null;
    }
  }

  /**
   * FHIR stores middle names as 2nd (and 3rd, 4th, .. nth) given names.
   * This takes the thinking out of storage.
   *
   * @param middle Middle name.
   */
  @JsonIgnore
  public void setMiddleName(String middle) {
    if (given == null || given.isEmpty()) {
      given = new ArrayList<>(Arrays.asList("", middle));
    } else {
      given.add(1, middle);
    }
  }

  /**
   * FHIR stores first name as the 0th given name.
   * This takes the thinking out of retrieval.
   *
   * @return The middle name, stored in second subscript of the given name.
   */
  @JsonIgnore
  public String getFirstName() {
    if (given != null && !given.isEmpty()) {
      return given.get(0);
    } else {
      return null;
    }
  }

  /**
   * FHIR stores first name as the 0th given name.
   * This takes the thinking out of storage.
   *
   * @param first First name.
   */
  @JsonIgnore
  public void setFirstName(String first) {
    if (given == null || given.isEmpty()) {
      given = new ArrayList<>(Arrays.asList(first));
    } else {
      given.set(0, first);
    }
  }

  /**
   * Handles the simplest case where a single last name must be set.
   *
   * @param last Last name.
   */
  @JsonIgnore
  public void setLastName(String last) {
    if (family == null || family.isEmpty()) {
      family = new ArrayList<>(Arrays.asList(last));
    } else {
      family.add(0, last);
    }
  }

  /**
   * This retrieves last name when it's assumed there is only one.
   *
   * @return The 0th subscript of the familyNames.
   */
  @JsonIgnore
  public String getLastName() {
    if (family != null && !family.isEmpty()) {
      return family.get(0);
    } else {
      return null;
    }
  }
}
